package com.tesi.gestionalec.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("COLLABORATORE")
@Data
public class Collaboratore extends Utente {

    /*
    // Pratiche assegnate a questo collaboratore
    @OneToMany(mappedBy = "collaboratore", cascade = CascadeType.ALL)
    private List<Pratica> praticheAssegnate;

    // Documenti che ha in carico per la revisione preliminare
    @OneToMany(mappedBy = "revisore", cascade = CascadeType.ALL)

    */

    @Override
    public Ruolo getRuolo() {
        return Ruolo.COLLABORATORE;
    }
}
