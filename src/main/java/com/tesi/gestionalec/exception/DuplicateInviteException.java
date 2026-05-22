package com.tesi.gestionalec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando si tenta di inviare un invito a un'email
 * per cui esiste già un invito PENDING da parte dello stesso commercialista.
 * Produce automaticamente un HTTP 409 Conflict.
 *
 * Esempio d'uso:
 *   throw new DuplicateInviteException("mario.rossi@example.com");
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateInviteException extends RuntimeException {

    private final String emailDestinatario;

    public DuplicateInviteException(String emailDestinatario) {
        super(String.format(
                "Esiste già un invito in attesa (PENDING) per l'email: '%s'.", emailDestinatario
        ));
        this.emailDestinatario = emailDestinatario;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }
}
