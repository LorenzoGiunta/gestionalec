package com.tesi.gestionalec.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MessaggioChatRequest {
    private Long destinatarioId;
    private String testo;
}