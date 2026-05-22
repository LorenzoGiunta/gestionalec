package com.tesi.gestionalec.controller;


import com.tesi.gestionalec.dto.request.LoginRequest;
import com.tesi.gestionalec.dto.request.RegistrazioneRequest;
import com.tesi.gestionalec.dto.response.AuthResponse;
import com.tesi.gestionalec.mapper.RegistrazioneMapper;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.security.GestoreTokenService;
import com.tesi.gestionalec.service.interfaces.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtenteService utenteService;
    private final GestoreTokenService gestoreToken;
    private final AuthenticationManager authManager;

    @PostMapping("/registra")
    public ResponseEntity<AuthResponse> registra(@Valid @RequestBody RegistrazioneRequest request){

        Utente utente = RegistrazioneMapper.toModel(request);
        Utente salvato = utenteService.registra(utente);
        String token = gestoreToken.generaToken(salvato.getEmail());

        return ResponseEntity.ok(new AuthResponse(
                token,
                salvato.getRuolo().name(),
                salvato.getEmail(),
                salvato.getNome(),
                salvato.getCognome()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Spring Security verifica email + password automaticamente
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Utente utente = (Utente) auth.getPrincipal();
        String token = gestoreToken.generaToken(utente.getEmail());

        return ResponseEntity.ok(new AuthResponse(
                token,
                utente.getRuolo().name(),
                utente.getEmail(),
                utente.getNome(),
                utente.getCognome()
        ));
    }
}
