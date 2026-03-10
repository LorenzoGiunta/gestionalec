package com.tesi.gestionalec.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
// Il "DiscriminatorValue" serve per sostituire "commercialista" al ruolo dello user
// Spring lo collega direttamente con l'annotation @Inheritance e @DiscriminatorColumn della classe User
@DiscriminatorValue("COMMERCIALISTA")
public class Commercialista extends Utente {

    private String numeroAlbo;

    @Override
    public Ruolo getRuolo() {
        return Ruolo.COMMERCIALISTA;
    }

    /* Da implementare bene quando facciamo la security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_COMMERCIALISTA"));
    }*/
}
