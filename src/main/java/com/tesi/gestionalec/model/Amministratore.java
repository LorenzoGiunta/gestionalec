package com.tesi.gestionalec.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("AMMINISTRATORE")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Amministratore extends Utente {

    // Data dell'ultima operazione amministrativa eseguita
    private LocalDateTime ultimaAzioneAmministrativa;

    @Override
    public Ruolo getRuolo() {
        return Ruolo.AMMINISTRATORE;
    }
}
