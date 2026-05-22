package com.tesi.gestionalec.controller;


import com.tesi.gestionalec.service.interfaces.CommercialistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commercialista")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_COMMERCIALISTA')")
public class CommercialistaController {

    private final CommercialistaService commercialistaService;

    @GetMapping("/imposte/{clienteId}")
    public ResponseEntity<Double> calcolaImposte(@PathVariable Long clienteId) {
        return ResponseEntity.ok(commercialistaService.calcolaImposteCliente(clienteId));
    }
}