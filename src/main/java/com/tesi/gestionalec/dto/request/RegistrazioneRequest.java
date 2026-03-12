package com.tesi.gestionalec.dto.request;

import com.tesi.gestionalec.model.Ruolo;
import lombok.Data;

@Data
public class RegistrazioneRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Ruolo ruolo;

    // campi specifici Cliente — null se ruolo != CLIENTE
    private String codFiscale;
    private String pIVA;
    private String regime;
    private Double redditoAnnuo;

    // campo specifico Commercialista — null se ruolo != COMMERCIALISTA
    private String numeroAlbo;
}
