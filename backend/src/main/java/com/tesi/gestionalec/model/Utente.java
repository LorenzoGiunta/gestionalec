package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entità base per tutti gli utenti del sistema.
 *
 * SOFT DELETE: il campo {@code deleted} viene impostato a {@code true} al posto
 * dell'eliminazione fisica. L'annotazione {@code @SQLRestriction} fa sì che
 * Hibernate aggiunga automaticamente {@code WHERE deleted = false} a TUTTE
 * le query su questa entità (findAll, findById, JPQL, ecc.), rendendo gli
 * utenti eliminati completamente invisibili senza dover modificare le query.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ruolo", discriminatorType = DiscriminatorType.STRING)
@SQLRestriction("deleted = false")   // ← nasconde automaticamente gli utenti cancellati
@Getter
@Setter
@ToString
public abstract class Utente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    @Column(nullable = false)
    private String password;

    private boolean enabled;

    // ── Soft Delete ──────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean deleted = false;        // false di default → utente attivo

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;        // timestamp dell'eliminazione logica
    // ────────────────────────────────────────────────────────────────────────

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