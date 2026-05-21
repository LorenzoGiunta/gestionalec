package com.tesi.gestionalec.dto.request;

import com.tesi.gestionalec.model.Ruolo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrazioneRequest {
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).{8,}$", 
             message = "La password deve contenere almeno 8 caratteri, una lettera maiuscola, una minuscola, un numero e un carattere speciale")
    private String password;

    @NotNull(message = "Il ruolo è obbligatorio")
    private Ruolo ruolo;

    // campi specifici Cliente — null se ruolo != CLIENTE
    @Pattern(regexp = "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$", message = "Codice fiscale non valido")
    private String codFiscale;

    @Pattern(regexp = "^[0-9]{11}$", message = "Partita IVA non valida")
    private String pIVA;
    
    private String regime;

    @PositiveOrZero(message = "Il reddito annuo non può essere negativo")
    private Double redditoAnnuo;

    // campo specifico Commercialista — null se ruolo != COMMERCIALISTA
    private String numeroAlbo;
}
