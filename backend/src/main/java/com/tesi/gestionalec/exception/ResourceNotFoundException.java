package com.tesi.gestionalec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione lanciata quando una risorsa non viene trovata nel database.
 * Produce automaticamente un HTTP 404 Not Found.
 *
 * Esempi d'uso:
 *   throw new ResourceNotFoundException("Utente", "id", 42L);
 *   throw new ResourceNotFoundException("Pratica", "id", praticaId);
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String nomeRisorsa;
    private final String nomeCampo;
    private final Object valoreCampo;

    public ResourceNotFoundException(String nomeRisorsa, String nomeCampo, Object valoreCampo) {
        super(String.format("%s non trovato/a con %s: '%s'", nomeRisorsa, nomeCampo, valoreCampo));
        this.nomeRisorsa = nomeRisorsa;
        this.nomeCampo = nomeCampo;
        this.valoreCampo = valoreCampo;
    }

    public String getNomeRisorsa() {
        return nomeRisorsa;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public Object getValoreCampo() {
        return valoreCampo;
    }
}
