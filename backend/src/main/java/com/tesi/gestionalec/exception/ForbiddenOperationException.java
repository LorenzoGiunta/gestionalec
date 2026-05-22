package com.tesi.gestionalec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando un utente autenticato tenta di eseguire
 * un'operazione su una risorsa di cui non è proprietario o non è
 * il destinatario corretto (controllo di ownership a livello applicativo,
 * diverso da @PreAuthorize che controlla i ruoli).
 * Produce automaticamente un HTTP 403 Forbidden.
 *
 * Esempi d'uso:
 *   throw new ForbiddenOperationException("Non sei il proprietario di questo invito");
 *   throw new ForbiddenOperationException("La tua email non corrisponde al destinatario");
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
