package com.tesi.gestionalec.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("AMMINISTRATORE")
@Data
public class Amministratore extends Utente {

    // Data dell'ultima operazione amministrativa eseguita
    private LocalDateTime ultimaAzioneAmministrativa;

    @Override
    public Ruolo getRuolo() {
        return Ruolo.AMMINISTRATORE;
    }
}
