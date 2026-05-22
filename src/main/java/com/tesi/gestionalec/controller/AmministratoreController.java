package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.response.UtenteResponse;
import com.tesi.gestionalec.mapper.UtenteMapper;
import com.tesi.gestionalec.service.interfaces.AmministratoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_AMMINISTRATORE')")  // ← tutti i metodi richiedono ruolo admin
public class AmministratoreController {

    private final AmministratoreService amministratoreService;


    // TO-DO : Solo l'amministratore può creare altri amministratori

    /**
     * GET /api/admin/utenti?page=0&size=20&sort=id,asc
     * Restituisce tutti gli utenti in modo paginato (default: 20 elementi).
     */
    @GetMapping("/utenti")
    public ResponseEntity<Page<UtenteResponse>> trovaTuttiUtenti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.fromString(sort.length > 1 ? sort[1] : "asc"), sort[0])
        ));
        Page<UtenteResponse> risultato = amministratoreService.trovaTutti(pageable)
                .map(UtenteMapper::toResponse);
        return ResponseEntity.ok(risultato);
    }

    @GetMapping("/utenti/{id}")
    public ResponseEntity<UtenteResponse> trovaPerId(@PathVariable Long id) {
        return ResponseEntity.ok(UtenteMapper.toResponse(amministratoreService.trovaPerId(id)));
    }

    @PutMapping("/utenti/{id}/abilita")
    public ResponseEntity<Void> abilita(@PathVariable Long id) {
        amministratoreService.abilitaUtente(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/utenti/{id}/disabilita")
    public ResponseEntity<Void> disabilita(@PathVariable Long id) {
        amministratoreService.disabilitaUtente(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/utenti/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Long id) {
        amministratoreService.eliminaUtente(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/admin/utenti/{id}/ripristina
     * Annulla la soft delete: riporta l'utente allo stato attivo (deleted=false, enabled=true).
     */
    @PutMapping("/utenti/{id}/ripristina")
    public ResponseEntity<Void> ripristina(@PathVariable Long id) {
        amministratoreService.ripristinaUtente(id);
        return ResponseEntity.ok().build();
    }
}