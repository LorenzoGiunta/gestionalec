package com.tesi.gestionalec.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MessaggioChatResponse {
    private Long id;
    private Long mittenteId;
    private String mittenteNome;
    private Long destinatarioId;
    private String destinatarioNome;
    private String testo;
    private boolean letto;
    private LocalDateTime dataInvio;
}