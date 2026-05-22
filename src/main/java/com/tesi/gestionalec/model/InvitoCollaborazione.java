package com.tesi.gestionalec.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tabella pivot che modella la relazione Many-to-Many tra Commercialista e Collaboratore.
 * Gestisce il ciclo di vita dell'invito: PENDING → ACCEPTED | DECLINED | EXPIRED.
 *
 * NOTA: collaboratore è nullable — un invito può essere inviato a un'email
 * non ancora registrata nel sistema. Il collegamento avviene al momento dell'accettazione.
 */
@Entity
@Table(
    name = "invito_collaborazione",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_invito_comm_email",
        columnNames = {"commercialista_id", "email_destinatario"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"commercialista", "collaboratore"})
public class InvitoCollaborazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Chi ha inviato l'invito */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commercialista_id", nullable = false)
    private Commercialista commercialista;

    /**
     * Chi ha ricevuto l'invito.
     * Null finché il destinatario non ha ancora un account nel sistema.
     * Valorizzato all'atto dell'accettazione (o al momento dell'invio se già registrato).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaboratore_id")
    private Collaboratore collaboratore;

    /** Email del destinatario — sempre valorizzata, usata per inviare il link */
    @Column(name = "email_destinatario", nullable = false)
    private String emailDestinatario;

    /** Token UUID univoco per il link di accettazione/rifiuto nell'email */
    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoInvito stato;

    @CreationTimestamp
    @Column(name = "creato_il", nullable = false, updatable = false)
    private LocalDateTime creatoIl;

    /** Data di scadenza: impostata a +7 giorni dalla creazione dal service */
    @Column(name = "scade_il", nullable = false)
    private LocalDateTime scadeIl;
}
