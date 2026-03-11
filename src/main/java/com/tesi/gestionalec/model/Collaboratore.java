package com.tesi.gestionalec.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@DiscriminatorValue("COLLABORATORE")
@Getter
@Setter
@ToString(exclude = {"praticheAssegnate" , "documentoList"})
@AllArgsConstructor
@NoArgsConstructor
public class Collaboratore extends Utente {

    // Pratiche assegnate a questo collaboratore
    @OneToMany(mappedBy = "assegnataA", cascade = CascadeType.ALL)
    private List<Pratica> praticheAssegnate;

    // Documenti che ha in carico per la revisione preliminare
    @OneToMany(mappedBy = "revisore", cascade = CascadeType.ALL)
    private List<Documento> documentoList;

    @Override
    public Ruolo getRuolo() {
        return Ruolo.COLLABORATORE;
    }
}
