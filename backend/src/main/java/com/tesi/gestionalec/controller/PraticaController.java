package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.request.PraticaRequest;
import com.tesi.gestionalec.dto.response.PraticaResponse;
import com.tesi.gestionalec.mapper.PraticaMapper;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pratiche")
@RequiredArgsConstructor
public class PraticaController {

    private final PraticaService praticaService;
    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<PraticaResponse> crea(@Valid @RequestBody PraticaRequest request) {
        Cliente cliente = (Cliente) clienteService.trovaPerId(request.getClienteId());
        Pratica pratica = PraticaMapper.toModel(request, cliente);
        return ResponseEntity.ok(PraticaMapper.toResponse(praticaService.creaPratica(pratica)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIALISTA', 'ROLE_COLLABORATORE', 'ROLE_CLIENTE')")
    public ResponseEntity<PraticaResponse> trovaPerId(@PathVariable Long id) {
        return ResponseEntity.ok(PraticaMapper.toResponse(praticaService.trovaPerId(id)));
    }

    /**
     * GET /api/pratiche?page=0&size=20&sort=id,desc
     * Restituisce le pratiche in modo paginato (default: 20 elementi, ordinati per id desc).
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Page<PraticaResponse>> trovaTutte(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        Page<PraticaResponse> risultato = praticaService.trovaTutte(pageable)
                .map(PraticaMapper::toResponse);
        return ResponseEntity.ok(risultato);
    }

    @PutMapping("/{id}/avanza")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> avanzaStato(@PathVariable Long id) {
        praticaService.avanzaStato(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{praticaId}/assegna/{collaboratoreId}")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> assegnaCollaboratore(@PathVariable Long praticaId,
                                                     @PathVariable Long collaboratoreId) {
        praticaService.assegnaCollaboratore(praticaId, collaboratoreId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/per-stato")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<?> trovaPerStato(@RequestParam StatoPratica stato) {
        return ResponseEntity.ok(praticaService.trovaPerStato(stato)
                .stream().map(PraticaMapper::toResponse).toList());
    }

    /**
     * DELETE /api/pratiche/{id}
     * Soft delete: imposta deleted=true nel DB.
     * La pratica e i suoi documenti rimangono archiviati per storico fiscale.
     * Solo il commercialista può eliminare pratiche.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        praticaService.eliminaPratica(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Helper per il parsing dell'ordinamento ───────────────────────────
    private Sort.Order[] parseSort(String[] sort) {
        if (sort.length == 2) {
            return new Sort.Order[]{new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0])};
        }
        // supporto multi-sort: sort=campo1,dir1&sort=campo2,dir2
        Sort.Order[] orders = new Sort.Order[sort.length / 2];
        for (int i = 0; i < sort.length; i += 2) {
            orders[i / 2] = new Sort.Order(
                    i + 1 < sort.length ? Sort.Direction.fromString(sort[i + 1]) : Sort.Direction.ASC,
                    sort[i]
            );
        }
        return orders;
    }
}