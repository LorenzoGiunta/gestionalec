package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Data
public class Pratica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPratica tipoPratica;  // es. DICHIARAZIONE_REDDIT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPratica stato;       // BOZZA, IN_LAVORAZIONE, IN_ATTESA_DOCUMENTI, COMPLETATA


    @ManyToOne
    @JoinColumn(name = "assegnata_a_id")
    private Collaboratore assegnataA;  // può essere null se non ancora assegnata

    @OneToMany(mappedBy = "pratica", cascade = CascadeType.ALL)
    private List<Documento> listaDocumenti;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCreazione;
}
