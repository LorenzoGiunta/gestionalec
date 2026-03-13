package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ruolo", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@ToString
public abstract class Utente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean enabled;

    // espone la colonna discriminante come campo leggibile
    @Column(name = "ruolo", insertable = false, updatable = false)
    private String ruoloDiscriminante;

    public abstract Ruolo getRuolo();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Ruolo r = getRuolo();
        if (r == null) return List.of();   // evita NullPointerException durante startup
        return List.of(new SimpleGrantedAuthority("ROLE_" + r.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}