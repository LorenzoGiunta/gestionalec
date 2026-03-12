package com.tesi.gestionalec.dto.response;

import com.tesi.gestionalec.model.Ruolo;
import lombok.Data;

@Data
public class UtenteResponse {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo;
    private boolean enabled;
}
