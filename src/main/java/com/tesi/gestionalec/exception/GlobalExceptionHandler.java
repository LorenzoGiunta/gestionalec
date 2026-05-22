package com.tesi.gestionalec.exception;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║              GESTORE GLOBALE DELLE ECCEZIONI — StudioFiscale         ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * Intercetta TUTTE le eccezioni lanciate dai controller e dai service,
 * le trasforma in risposte JSON standard (ApiError) con il codice HTTP
 * corretto, senza mai esporre stacktrace o dettagli interni all'utente.
 *
 * Gerarchia di gestione (dall'alto = più specifica → in basso = fallback):
 *
 *  ① MethodArgumentNotValidException  → 400 Bad Request  (validazione @Valid)
 *  ② ResourceNotFoundException        → 404 Not Found    (risorsa assente)
 *  ③ EmailAlreadyExistsException      → 409 Conflict     (email duplicata)
 *  ④ DuplicateInviteException         → 409 Conflict     (invito duplicato)
 *  ⑤ InvalidStateException            → 409 Conflict     (transizione di stato invalida)
 *  ⑥ ForbiddenOperationException      → 403 Forbidden    (ownership violation)
 *  ⑦ AccessDeniedException            → 403 Forbidden    (ruolo insufficiente)
 *  ⑧ EntityNotFoundException          → 404 Not Found    (JPA legacy)
 *  ⑨ BadCredentialsException          → 401 Unauthorized (login fallito)
 *  ⑩ DisabledException                → 401 Unauthorized (account disabilitato)
 *  ⑪ Exception (fallback)             → 500 Internal Server Error
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────────────────────────────────
    // ① Errori di Validazione (@Valid / @Validated)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gestisce le eccezioni generate quando i DTO annotati con @Valid
     * falliscono la validazione (es. campo email vuoto, nome troppo corto).
     * Restituisce la mappa completa di tutti i campi invalidi, così il
     * frontend può evidenziare ogni campo errato nel form.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Raccoglie tutti i FieldError e li trasforma in una mappa campo → messaggio
        Map<String, String> campiInvalidi = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Valore non valido",
                        // in caso di messaggi duplicati sullo stesso campo, tiene il primo
                        (msg1, msg2) -> msg1
                ));

        log.warn("[VALIDAZIONE] Richiesta non valida su {}: {}", request.getRequestURI(), campiInvalidi);

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Dati non validi",
                "La richiesta contiene " + campiInvalidi.size() + " campo/i non valido/i.",
                request.getRequestURI()
        );
        error.setCampiInvalidi(campiInvalidi);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ①bis Argomenti non validi — IllegalArgumentException (400)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gestisce eccezioni come il path-traversal check nel FileStorageService
     * e qualsiasi altro IllegalArgumentException lanciato nel codice applicativo.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("[BAD REQUEST] {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Richiesta non valida",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ② Risorsa Non Trovata (404)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("[NOT FOUND] {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Risorsa non trovata",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ③ File Non Trovato su Disco (404)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiError> handleFileNotFoundException(
            FileNotFoundException ex,
            HttpServletRequest request) {

        log.warn("[NOT FOUND - FILE] {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "File non trovato",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ④ Email Già Esistente (409)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        log.warn("[CONFLITTO] Email duplicata su {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Conflitto dati",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ④ Invito Duplicato (409)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(DuplicateInviteException.class)
    public ResponseEntity<ApiError> handleDuplicateInviteException(
            DuplicateInviteException ex,
            HttpServletRequest request) {

        log.warn("[CONFLITTO] Invito duplicato su {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Invito duplicato",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑤ Transizione di Stato Invalida (409)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiError> handleInvalidStateException(
            InvalidStateException ex,
            HttpServletRequest request) {

        log.warn("[STATO INVALIDO] {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Operazione non consentita",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑥ Violazione di Ownership (403 applicativo)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleForbiddenOperationException(
            ForbiddenOperationException ex,
            HttpServletRequest request) {

        log.warn("[FORBIDDEN] Tentativo di operazione non autorizzata su {}: {}",
                request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "Operazione non autorizzata",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑦ Accesso Negato da Spring Security — Ruolo Insufficiente (403)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Intercetta AccessDeniedException lanciata da @PreAuthorize quando
     * l'utente è autenticato ma non ha il ruolo richiesto.
     * IMPORTANTE: Spring Security di default gestisce questa eccezione prima
     * di passarla al ControllerAdvice. Per funzionare correttamente è necessario
     * che la SecurityConfig NON abbia un AccessDeniedHandler personalizzato che
     * interrompa il flusso, oppure che lo rimandi qui.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("[ACCESSO NEGATO] Tentativo di accesso non autorizzato su {} ({})",
                request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "Accesso negato",
                "Non hai i permessi necessari per eseguire questa operazione.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑧ EntityNotFoundException di JPA (404) — per compatibilità con codice legacy
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        log.warn("[NOT FOUND - JPA] {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Risorsa non trovata",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑨ Credenziali Errate — Login Fallito (401)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("[AUTH] Tentativo di login fallito su {}", request.getRequestURI());

        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenziali non valide",
                "Email o password non corretti. Riprova.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑩ Account Disabilitato (401)
    // ─────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(
            DisabledException ex,
            HttpServletRequest request) {

        log.warn("[AUTH] Tentativo di accesso con account disabilitato su {}", request.getRequestURI());

        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "Account disabilitato",
                "Il tuo account è stato disabilitato. Contatta l'amministratore.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ⑪ Fallback Generico — Qualsiasi altra eccezione non gestita (500)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cattura TUTTO ciò che sfugge agli handler specifici.
     * Logga l'eccezione completa (con stacktrace) SOLO nel log del server,
     * ma NON la espone mai al client — il client riceve solo un messaggio generico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        // Il log è a livello ERROR per avere traccia completa sul server
        log.error("[ERRORE INTERNO] Eccezione non gestita su {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Errore interno del server",
                "Si è verificato un errore imprevisto. Riprova più tardi o contatta il supporto.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
