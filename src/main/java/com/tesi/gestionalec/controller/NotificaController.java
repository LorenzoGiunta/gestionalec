package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.response.NotificaResponse;
import com.tesi.gestionalec.mapper.NotificaMapper;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.service.interfaces.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {

    private final NotificaService notificaService;

    @GetMapping("/mie")
    public ResponseEntity<List<NotificaResponse>> mie(@AuthenticationPrincipal Utente utente) {
        return ResponseEntity.ok(notificaService.trovaPerUtente(utente)
                .stream()
                .map(NotificaMapper::toResponse)
                .toList());
    }

    @GetMapping("/non-lette")
    public ResponseEntity<List<NotificaResponse>> nonLette(@AuthenticationPrincipal Utente utente) {
        return ResponseEntity.ok(notificaService.trovaNonLette(utente)
                .stream()
                .map(NotificaMapper::toResponse)
                .toList());
    }

    @PutMapping("/{id}/letta")
    public ResponseEntity<Void> segnaComeLetta(@PathVariable Long id) {
        notificaService.segnaComeLetta(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/letta-tutte")
    public ResponseEntity<Void> segnaComeLetteTutte(@AuthenticationPrincipal Utente utente) {
        notificaService.segnaComeLetteTutte(utente.getId());
        return ResponseEntity.ok().build();
    }
}