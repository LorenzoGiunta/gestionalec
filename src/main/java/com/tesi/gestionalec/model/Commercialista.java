package com.tesi.gestionalec.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
// Il "DiscriminatorValue" serve per sostituire "commercialista" al ruolo dello user
// Spring lo collega direttamente con l'annotation @Inheritance e @DiscriminatorColumn della classe User
@DiscriminatorValue("COMMERCIALISTA")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Commercialista extends Utente {

    private String numeroAlbo;

    @Override
    public Ruolo getRuolo() {
        return Ruolo.COMMERCIALISTA;
    }
}
