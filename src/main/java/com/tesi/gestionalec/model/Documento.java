package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"pratica", "caricatoDa", "revisore"})
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipoFile;        // es. "application/pdf"

    @Column(nullable = false)
    private String percorsoFile;

    private Long dimensione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoDocumento stato;   // IN_REVISIONE, APPROVATO, RIFIUTATO

    @Column(nullable = true)
    private String motivazioneRifiuto;  // compilato solo se RIFIUTATO

    // Versionamento
    @Column(nullable = false)
    private Integer versione;       // parte da 1, incrementa ad ogni upload

    // Relazioni
    @ManyToOne
    @JoinColumn(name = "pratica_id", nullable = false)
    private Pratica pratica;

    @ManyToOne
    @JoinColumn(name = "caricato_da_id", nullable = false)
    private Cliente caricatoDa;

    @ManyToOne
    @JoinColumn(name = "revisore_id")
    private Collaboratore revisore;  // assegnato per la revisione preliminare

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCaricamento;
}
