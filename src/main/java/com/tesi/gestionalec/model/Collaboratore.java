package com.tesi.gestionalec.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("COLLABORATORE")
@Getter
@Setter
@ToString(exclude = {"praticheAssegnate" , "documentiInRevisione"})
@AllArgsConstructor
@NoArgsConstructor
public class Collaboratore extends Utente {

    // Pratiche assegnate a questo collaboratore
    @OneToMany(mappedBy = "assegnataA", cascade = CascadeType.ALL)
    private List<Pratica> praticheAssegnate;

    // Documenti che ha in carico per la revisione preliminare
    @OneToMany(mappedBy = "revisore", cascade = CascadeType.ALL)
    private List<Documento> documentiInRevisione;

    // Tutte le associazioni (PENDING, ACCEPTED, DECLINED, EXPIRED) con i commercialisti
    @OneToMany(mappedBy = "collaboratore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvitoCollaborazione> associazioni = new ArrayList<>();

    /**
     * Restituisce solo i Commercialisti con cui l'associazione è ACCEPTED.
     * Metodo di comodo — evita query esplicite nel service per il caso comune.
     */
    public List<Commercialista> getCommercialisti() {
        return associazioni.stream()
                .filter(i -> i.getStato() == StatoInvito.ACCEPTED)
                .map(InvitoCollaborazione::getCommercialista)
                .toList();
    }

    @Override
    public Ruolo getRuolo() {
        return Ruolo.COLLABORATORE;
    }
}
