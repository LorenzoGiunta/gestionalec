package com.tesi.gestionalec.model;


import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "Role")
public abstract class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    private String Nome;
    private String Cognome;

    @Column(unique = true , nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // se per caso l'admin vuole disattivare l'utente
    private boolean enabled;

    public abstract Ruolo getRuolo();

}
