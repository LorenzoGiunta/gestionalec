package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.response.NotificaResponse;
import com.tesi.gestionalec.mapper.NotificaMapper;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.service.interfaces.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {

    private final NotificaService notificaService;

    /**
     * GET /api/notifiche/mie?page=0&size=20
     * Notifiche dell'utente autenticato, paginate (default 20, più recenti prima).
     */
    @GetMapping("/mie")
    public ResponseEntity<Page<NotificaResponse>> mie(
            @AuthenticationPrincipal Utente utente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<NotificaResponse> risultato = notificaService.trovaPerUtente(utente, pageable)
                .map(NotificaMapper::toResponse);
        return ResponseEntity.ok(risultato);
    }

    /**
     * GET /api/notifiche/non-lette?page=0&size=20
     * Solo notifiche non lette, paginate.
     */
    @GetMapping("/non-lette")
    public ResponseEntity<Page<NotificaResponse>> nonLette(
            @AuthenticationPrincipal Utente utente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<NotificaResponse> risultato = notificaService.trovaNonLette(utente, pageable)
                .map(NotificaMapper::toResponse);
        return ResponseEntity.ok(risultato);
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