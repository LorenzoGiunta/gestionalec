package com.tesi.gestionalec.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO standard per tutte le risposte di errore dell'API.
 * Garantisce un formato JSON uniforme e privo di stacktrace
 * per qualsiasi tipo di eccezione gestita.
 *
 * Esempio JSON prodotto:
 * {
 *   "timestamp": "2024-01-15T10:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Utente non trovato con id: '42'",
 *   "path": "/api/utenti/42",
 *   "campiInvalidi": null    ← assente in caso di errori non-validation
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)  // omette campi null dalla risposta JSON
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    private final int status;
    private final String error;
    private final String message;
    private final String path;

    /**
     * Presente solo per errori di validazione (@Valid).
     * Mappa: nome del campo → messaggio di errore.
     * Es: { "email": "deve essere un indirizzo email valido", "nome": "non deve essere vuoto" }
     */
    private Map<String, String> campiInvalidi;

    public ApiError(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // ─── Getters (Jackson li usa per serializzare) ────────────────────────────

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus()              { return status; }
    public String getError()            { return error; }
    public String getMessage()          { return message; }
    public String getPath()             { return path; }
    public Map<String, String> getCampiInvalidi() { return campiInvalidi; }

    // ─── Setter solo per campiInvalidi ────────────────────────────────────────

    public void setCampiInvalidi(Map<String, String> campiInvalidi) {
        this.campiInvalidi = campiInvalidi;
    }
}
