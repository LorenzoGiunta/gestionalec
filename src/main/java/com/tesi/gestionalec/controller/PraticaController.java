package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.request.PraticaRequest;
import com.tesi.gestionalec.dto.response.PraticaResponse;
import com.tesi.gestionalec.mapper.PraticaMapper;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pratiche")
@RequiredArgsConstructor
public class PraticaController {

    private final PraticaService praticaService;
    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<PraticaResponse> crea(@RequestBody PraticaRequest request) {
        Cliente cliente = (Cliente) clienteService.trovaPerId(request.getClienteId());
        Pratica pratica = PraticaMapper.toModel(request, cliente);
        return ResponseEntity.ok(PraticaMapper.toResponse(praticaService.creaPratica(pratica)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_COMMERCIALISTA', 'ROLE_COLLABORATORE', 'ROLE_CLIENTE')")
    public ResponseEntity<PraticaResponse> trovaPerId(@PathVariable Long id) {
        return ResponseEntity.ok(PraticaMapper.toResponse(praticaService.trovaPerId(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<List<PraticaResponse>> trovaTutte() {
        return ResponseEntity.ok(praticaService.trovaTutte()
                .stream()
                .map(PraticaMapper::toResponse)
                .toList());
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
}