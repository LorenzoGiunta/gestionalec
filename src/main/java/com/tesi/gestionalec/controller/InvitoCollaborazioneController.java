package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.request.InvitoRequest;
import com.tesi.gestionalec.dto.response.InvitoResponse;
import com.tesi.gestionalec.mapper.InvitoMapper;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.service.interfaces.InvitoCollaborazioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per la gestione degli inviti di collaborazione.
 *
 * Endpoint suddivisi per ruolo:
 *   - COMMERCIALISTA: invita, visualizza inviti, revoca
 *   - COLLABORATORE:  visualizza inviti pending, accetta
 *   - Pubblico:       rifiuta (via link email senza autenticazione obbligatoria)
 */
@RestController
@RequestMapping("/api/inviti")
@RequiredArgsConstructor
public class InvitoCollaborazioneController {

    private final InvitoCollaborazioneService invitoService;

    // ═══════════════════════════════════════════════════════════════════════
    // COMMERCIALISTA
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * POST /api/inviti
     * Il Commercialista invia un invito a un collaboratore per email.
     * Genera token, salva l'invito PENDING e invia l'email asincrona.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<InvitoResponse> invita(
            @AuthenticationPrincipal Utente utente,
            @Valid @RequestBody InvitoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(InvitoMapper.toResponse(
                        invitoService.invita(utente.getId(), request.getEmailDestinatario())
                ));
    }

    /**
     * GET /api/inviti/miei
     * Ritorna tutti gli inviti inviati dal Commercialista autenticato (qualsiasi stato).
     * Il frontend usa questa lista per mostrare la tabella collaboratori con badge di stato.
     */
    @GetMapping("/miei")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<List<InvitoResponse>> miei(@AuthenticationPrincipal Utente utente) {
        List<InvitoResponse> lista = invitoService.trovaPerCommercialista(utente.getId())
                .stream()
                .map(InvitoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * DELETE /api/inviti/{id}
     * Revoca un'associazione (ACCEPTED → DECLINED) oppure cancella un invito PENDING.
     * Solo il Commercialista proprietario può eseguire questa operazione.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
    public ResponseEntity<Void> revoca(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long id) {

        invitoService.revoca(id, utente.getId());
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // COLLABORATORE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/inviti/pending
     * Ritorna gli inviti PENDING indirizzati all'email del Collaboratore autenticato.
     * Chiamato al login o all'apertura della dashboard per mostrare il badge notifiche.
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_COLLABORATORE')")
    public ResponseEntity<List<InvitoResponse>> pending(@AuthenticationPrincipal Utente utente) {
        List<InvitoResponse> lista = invitoService.trovaPendingPerEmail(utente.getEmail())
                .stream()
                .map(InvitoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * POST /api/inviti/{token}/accetta
     * Il Collaboratore autenticato accetta l'invito identificato dal token.
     * Verifica che l'email dell'utente corrisponda al destinatario dell'invito.
     */
    @PostMapping("/{token}/accetta")
    @PreAuthorize("hasAuthority('ROLE_COLLABORATORE')")
    public ResponseEntity<Void> accetta(
            @PathVariable String token,
            @AuthenticationPrincipal Utente utente) {

        invitoService.accetta(token, utente.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/inviti/{token}/rifiuta
     * Rifiuta l'invito tramite token. Non richiede autenticazione:
     * l'utente può rifiutare direttamente dal link nell'email, anche senza account.
     */
    @PostMapping("/{token}/rifiuta")
    public ResponseEntity<Void> rifiuta(@PathVariable String token) {
        invitoService.rifiuta(token);
        return ResponseEntity.ok().build();
    }
}
