package com.tesi.gestionalec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando si tenta un'operazione non consentita
 * a causa dello stato corrente di una risorsa (es. avanzare una pratica
 * già completata, revocare un invito già scaduto, ecc.).
 * Produce automaticamente un HTTP 409 Conflict.
 *
 * Esempi d'uso:
 *   throw new InvalidStateException("Impossibile revocare un invito nello stato EXPIRED");
 *   throw new InvalidStateException("La pratica è già nello stato finale: COMPLETATA");
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String resourceName, String currentState, String operation) {
        super(String.format(
                "Operazione '%s' non consentita su '%s' nello stato '%s'.",
                operation, resourceName, currentState
        ));
    }
}
