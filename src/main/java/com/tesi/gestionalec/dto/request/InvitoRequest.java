package com.tesi.gestionalec.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Corpo della richiesta POST /api/inviti
 * Inviata dal Commercialista per invitare un collaboratore.
 */
@Data
public class InvitoRequest {

    @NotBlank(message = "L'email del destinatario è obbligatoria")
    @Email(message = "Formato email non valido")
    private String emailDestinatario;
}
