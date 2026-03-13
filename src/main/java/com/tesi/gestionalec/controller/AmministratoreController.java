package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.response.UtenteResponse;
import com.tesi.gestionalec.mapper.UtenteMapper;
import com.tesi.gestionalec.service.interfaces.AmministratoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_AMMINISTRATORE')")  // ← tutti i metodi richiedono ruolo admin
public class AmministratoreController {

    private final AmministratoreService amministratoreService;



    // TO-DO : Solo l'amministratore può creare altri amministratori

    @GetMapping("/utenti")
    public ResponseEntity<List<UtenteResponse>> trovaTuttiUtenti() {
        return ResponseEntity.ok(amministratoreService.trovaTutti()
                .stream()
                .map(UtenteMapper::toResponse)
                .toList());
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
}