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
@DiscriminatorColumn(name = "ruolo")
@Getter
@Setter
@ToString
public abstract class Utente implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cognome;

    @Column(unique = true , nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // se per caso l'admin vuole disattivare l'utente
    private boolean enabled;

    public abstract Ruolo getRuolo();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRuolo().name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
