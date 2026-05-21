package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * Documento fiscale allegato a una Pratica.
 *
 * SOFT DELETE: il campo {@code deleted} viene impostato a {@code true} al posto
 * dell'eliminazione fisica. L'annotazione {@code @SQLRestriction} fa sì che
 * Hibernate aggiunga automaticamente {@code WHERE deleted = false} a tutte le
 * query su questa entità, preservando lo storico fiscale nel DB.
 */
@Entity
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"pratica", "caricatoDa", "revisore"})
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome del documento è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il tipo di file è obbligatorio")
    @Column(nullable = false)
    private String tipoFile;        // es. "application/pdf"

    @NotBlank(message = "Il percorso del file è obbligatorio")
    @Column(nullable = false)
    private String percorsoFile;

    @NotNull(message = "La dimensione del file è obbligatoria")
    @Positive(message = "La dimensione deve essere positiva")
    private Long dimensione;

    @NotNull(message = "Lo stato del documento è obbligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoDocumento stato;   // IN_REVISIONE, APPROVATO, RIFIUTATO

    private String motivazioneRifiuto;  // compilato solo se RIFIUTATO

    // Versionamento
    @NotNull(message = "La versione del documento è obbligatoria")
    @Positive(message = "La versione deve essere positiva")
    @Column(nullable = false)
    private Integer versione;       // parte da 1, incrementa ad ogni upload

    // Relazioni
    @NotNull(message = "La pratica di riferimento è obbligatoria")
    @ManyToOne
    @JoinColumn(name = "pratica_id", nullable = false)
    private Pratica pratica;

    @NotNull(message = "L'utente che ha caricato il documento è obbligatorio")
    @ManyToOne
    @JoinColumn(name = "caricato_da_id", nullable = false)
    private Cliente caricatoDa;

    @ManyToOne
    @JoinColumn(name = "revisore")
    private Collaboratore revisore;  // assegnato per la revisione preliminare

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCaricamento;

    // ── Soft Delete ──────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean deleted = false;     // false di default → documento attivo

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;     // timestamp dell'eliminazione logica
    // ────────────────────────────────────────────────────────────────────────
}
