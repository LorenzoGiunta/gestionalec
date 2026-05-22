package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.response.DocumentoResponse;
import com.tesi.gestionalec.dto.response.PraticaResponse;
import com.tesi.gestionalec.mapper.DocumentoMapper;
import com.tesi.gestionalec.mapper.PraticaMapper;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CLIENTE')")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/pratiche")
    public ResponseEntity<List<PraticaResponse>> miePratiche(@AuthenticationPrincipal Utente utente) {
        return ResponseEntity.ok(clienteService.trovaPratiche(utente.getId())
                .stream()
                .map(PraticaMapper::toResponse)
                .toList());
    }

    @GetMapping("/documenti")
    public ResponseEntity<List<DocumentoResponse>> mieDocumenti(@AuthenticationPrincipal Utente utente) {
        return ResponseEntity.ok(clienteService.trovaDocumenti(utente.getId())
                .stream()
                .map(DocumentoMapper::toResponse)
                .toList());
    }
}