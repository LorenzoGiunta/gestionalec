package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.request.DocumentoRequest;
import com.tesi.gestionalec.dto.response.DocumentoResponse;
import com.tesi.gestionalec.mapper.DocumentoMapper;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import com.tesi.gestionalec.service.interfaces.DocumentoService;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documenti")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;
    private final PraticaService praticaService;
    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<DocumentoResponse> carica(@RequestBody DocumentoRequest request) {
        Pratica pratica = praticaService.trovaPerId(request.getPraticaId());
        Cliente cliente = (Cliente) clienteService.trovaPerId(request.getCaricatoDaId());
        Documento documento = DocumentoMapper.toModel(request, pratica, cliente);
        return ResponseEntity.ok(DocumentoMapper.toResponse(documentoService.caricaDocumento(documento)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIALISTA', 'ROLE_COLLABORATORE', 'ROLE_CLIENTE')")
    public ResponseEntity<DocumentoResponse> trovaPerId(@PathVariable Long id) {
        return ResponseEntity.ok(DocumentoMapper.toResponse(documentoService.trovaPerId(id)));
    }

    @PutMapping("/{id}/assegna-revisore/{collaboratoreId}")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> assegnaRevisore(@PathVariable Long id,
                                                @PathVariable Long collaboratoreId) {
        documentoService.assegnaRevisore(id, collaboratoreId);
        return ResponseEntity.ok().build();
    }
}