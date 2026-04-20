package com.tesi.gestionalec.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MessaggioChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mittente_id", nullable = false)
    private Utente mittente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Utente destinatario;

    @Column(nullable = false)
    private String testo;

    private boolean letto = false;

    @CreationTimestamp
    private LocalDateTime dataInvio;
}