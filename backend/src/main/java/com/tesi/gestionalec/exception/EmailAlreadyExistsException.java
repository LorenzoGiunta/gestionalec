package com.tesi.gestionalec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando si tenta di registrare un utente
 * con un'email già presente nel sistema.
 * Produce automaticamente un HTTP 409 Conflict.
 *
 * Esempio d'uso:
 *   throw new EmailAlreadyExistsException("mario.rossi@example.com");
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    private final String email;

    public EmailAlreadyExistsException(String email) {
        super(String.format("L'email '%s' è già registrata nel sistema.", email));
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
