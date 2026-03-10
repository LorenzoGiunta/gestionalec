package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "Ruolo")
@Getter
@Setter
@ToString
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
