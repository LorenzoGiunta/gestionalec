package com.tesi.gestionalec.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @NotBlank(message = "Il numero di albo è obbligatorio")
    private String numeroAlbo;

    // Tutti gli inviti inviati dal commercialista (tutti gli stati)
    @OneToMany(mappedBy = "commercialista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvitoCollaborazione> inviti = new ArrayList<>();

    /**
     * Restituisce solo i Collaboratori con associazione ACCEPTED.
     * Metodo di comodo — evita query esplicite nel service per il caso comune.
     */
    public List<Collaboratore> getCollaboratori() {
        return inviti.stream()
                .filter(i -> i.getStato() == StatoInvito.ACCEPTED)
                .map(InvitoCollaborazione::getCollaboratore)
                .toList();
    }

    @Override
    public Ruolo getRuolo() {
        return Ruolo.COMMERCIALISTA;
    }
}
