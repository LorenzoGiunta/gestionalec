package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifiche")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String messaggio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotifica tipo;      // CAMBIO_STATO, DOCUMENTO_CARICATO, SCADENZA

    private boolean letta;          // false di default — l'utente non l'ha ancora letta

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente destinatario;    // chi riceve la notifica

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCreazione;
}
