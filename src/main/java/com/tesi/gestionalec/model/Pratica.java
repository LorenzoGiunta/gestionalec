package com.tesi.gestionalec.model;


import com.tesi.gestionalec.state.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"listaDocumenti", "cliente", "assegnataA"})
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

    //non salvato nel DB, solo in memoria per lo State Pattern
    @Transient
    private StatoPraticaState statoCorrente;

    //ricostruisce statoCorrente ogni volta che JPA carica la Pratica
    @PostLoad
    private void inizializzaStato() {
        this.statoCorrente = switch (this.stato) {
            case BOZZA               -> new BozzaState();
            case IN_LAVORAZIONE      -> new InLavorazioneState();
            case IN_ATTESA_DOCUMENTI -> new InAttesaDocumentiState();
            case COMPLETATA          -> new CompletataState();
        };
    }
}
