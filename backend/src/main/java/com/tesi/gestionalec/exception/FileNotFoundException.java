package com.tesi.gestionalec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando un file richiesto non viene trovato su disco
 * oppure non è leggibile.
 * Produce automaticamente un HTTP 404 Not Found.
 *
 * Esempio d'uso:
 *   throw new FileNotFoundException("uploads/uuid_documento.pdf");
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException {

    private final String percorso;

    public FileNotFoundException(String percorso) {
        super(String.format("File non trovato o non leggibile: '%s'", percorso));
        this.percorso = percorso;
    }

    public String getPercorso() {
        return percorso;
    }
}
