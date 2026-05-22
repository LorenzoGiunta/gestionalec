package com.tesi.gestionalec.model;


import com.tesi.gestionalec.state.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Pratica fiscale aperta per un Cliente.
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
@ToString(exclude = {"listaDocumenti", "cliente", "assegnataA"})
public class Pratica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Il cliente è obbligatorio")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "Il tipo di pratica è obbligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPratica tipoPratica;  // es. DICHIARAZIONE_REDDIT

    @NotNull(message = "Lo stato della pratica è obbligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPratica stato;       // BOZZA, IN_LAVORAZIONE, IN_ATTESA_DOCUMENTI, COMPLETATA

    @ManyToOne
    @JoinColumn(name = "assegnata_a_id")
    private Collaboratore assegnataA;  // può essere null se non ancora assegnata

    private LocalDate scadenza;

    @OneToMany(mappedBy = "pratica", cascade = CascadeType.ALL)
    private List<Documento> listaDocumenti;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCreazione;

    //non salvato nel DB, solo in memoria per lo State Pattern
    @Transient
    private StatoPraticaState statoCorrente;

    // ── Soft Delete ──────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean deleted = false;     // false di default → pratica attiva

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;     // timestamp dell'eliminazione logica
    // ────────────────────────────────────────────────────────────────────────

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
