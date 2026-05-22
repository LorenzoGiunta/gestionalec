package com.tesi.gestionalec.dto.response;

import com.tesi.gestionalec.model.StatoInvito;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO di risposta per un InvitoCollaborazione.
 * Esposto sia al Commercialista (lista inviti inviati) che al Collaboratore (inviti ricevuti).
 */
@Data
@Builder
public class InvitoResponse {

    private Long id;

    /** Token UUID: serve al frontend per costruire il link di accettazione/rifiuto */
    private String token;

    private String emailDestinatario;

    /** Dati del Commercialista mittente */
    private Long commercialistaId;
    private String nomeCommercialista;      // "Mario Rossi"
    private String studioCommercialista;   // numero albo — utile per riconoscere lo studio

    /** Dati del Collaboratore (null se non ancora registrato) */
    private Long collaboratoreId;
    private String nomeCollaboratore;

    private StatoInvito stato;
    private LocalDateTime creatoIl;
    private LocalDateTime scadeIl;
}
