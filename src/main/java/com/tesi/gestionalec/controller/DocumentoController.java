package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.response.DocumentoResponse;
import com.tesi.gestionalec.mapper.DocumentoMapper;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.service.FileStorageService;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import com.tesi.gestionalec.service.interfaces.DocumentoService;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documenti")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;
    private final PraticaService praticaService;
    private final ClienteService clienteService;
    private final FileStorageService fileStorageService;

    /**
     * POST /api/documenti
     * Caricamento reale del file (multipart/form-data).
     * Il file viene salvato su disco; i metadati nel DB.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<DocumentoResponse> carica(
            @RequestPart("file")      MultipartFile file,
            @RequestPart("nome")      String nome,
            @RequestPart("tipoFile")  String tipoFile,
            @RequestPart("praticaId") String praticaId,
            @AuthenticationPrincipal Utente utente) throws IOException {

        // 1. Salva il file su disco → ottieni il percorso relativo
        String percorsoFile = fileStorageService.salva(file);

        // 2. Recupera entità collegate
        Pratica pratica = praticaService.trovaPerId(Long.parseLong(praticaId));
        Cliente cliente = (Cliente) clienteService.trovaPerId(utente.getId());

        // 3. Costruisce il Documento e lo persiste
        Documento documento = new Documento();
        documento.setNome(nome.isBlank() ? file.getOriginalFilename() : nome);
        documento.setTipoFile(tipoFile);
        documento.setPercorsoFile(percorsoFile);
        documento.setDimensione(file.getSize());
        documento.setPratica(pratica);
        documento.setCaricatoDa(cliente);

        return ResponseEntity.ok(
                DocumentoMapper.toResponse(documentoService.caricaDocumento(documento))
        );
    }

    /**
     * GET /api/documenti/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIALISTA', 'ROLE_COLLABORATORE', 'ROLE_CLIENTE')")
    public ResponseEntity<DocumentoResponse> trovaPerId(@PathVariable Long id) {
        return ResponseEntity.ok(DocumentoMapper.toResponse(documentoService.trovaPerId(id)));
    }

    /**
     * POST /api/documenti/{id}/nuova-versione
     * Carica una nuova versione di un documento esistente (multipart/form-data).
     *
     * Il service si occupa di:
     *   - incrementare automaticamente il numero di versione (vecchio.versione + 1)
     *   - reimpostare lo stato a IN_REVISIONE
     *   - ereditare pratica e cliente dal documento originale
     *
     * Il client deve inviare solo il nuovo file (e opzionalmente un nuovo nome).
     * La pratica e il cliente vengono copiati dal documento {id} esistente.
     */
    @PostMapping(value = "/{id}/nuova-versione", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<DocumentoResponse> nuovaVersione(
            @PathVariable Long id,
            @RequestPart("file")               MultipartFile file,
            @RequestPart(value = "nome", required = false) String nome,
            @RequestPart("tipoFile")            String tipoFile) throws IOException {

        // 1. Salva il nuovo file fisico su disco
        String percorsoFile = fileStorageService.salva(file);

        // 2. Costruisce il Documento con i nuovi metadati
        //    pratica, cliente e versione vengono ereditati dal service
        Documento nuovoDocumento = new Documento();
        nuovoDocumento.setNome((nome == null || nome.isBlank()) ? file.getOriginalFilename() : nome);
        nuovoDocumento.setTipoFile(tipoFile);
        nuovoDocumento.setPercorsoFile(percorsoFile);
        nuovoDocumento.setDimensione(file.getSize());

        // 3. Delega al service: incrementa versione, copia pratica/cliente, salva
        return ResponseEntity.ok(
                DocumentoMapper.toResponse(documentoService.nuovaVersione(id, nuovoDocumento))
        );
    }

    /**
     * GET /api/documenti/{id}/download
     * Scarica il file fisico dal disco.
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIALISTA', 'ROLE_COLLABORATORE', 'ROLE_CLIENTE')")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Documento doc = documentoService.trovaPerId(id);
        Resource resource = fileStorageService.carica(doc.getPercorsoFile());

        // Determina il Content-Type dal nome del file
        String contentType = "application/octet-stream";
        String nome = doc.getNome().toLowerCase();
        if (nome.endsWith(".pdf"))  contentType = "application/pdf";
        else if (nome.endsWith(".doc") || nome.endsWith(".docx"))
            contentType = "application/msword";
        else if (nome.endsWith(".xls") || nome.endsWith(".xlsx"))
            contentType = "application/vnd.ms-excel";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getNome() + "\"")
                .body(resource);
    }

    /**
     * PUT /api/documenti/{id}/assegna-revisore/{collaboratoreId}
     */
    @PutMapping("/{id}/assegna-revisore/{collaboratoreId}")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> assegnaRevisore(@PathVariable Long id,
                                                @PathVariable Long collaboratoreId) {
        documentoService.assegnaRevisore(id, collaboratoreId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/documenti/{id}
     * Soft delete: imposta deleted=true nel DB. Il file fisico rimane su disco.
     * Solo il commercialista può eliminare documenti.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        documentoService.eliminaDocumento(id);
        return ResponseEntity.noContent().build();
    }
}