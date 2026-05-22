package com.tesi.gestionalec.controller;

import com.tesi.gestionalec.dto.request.MessaggioChatRequest;
import com.tesi.gestionalec.dto.response.MessaggioChatResponse;
import com.tesi.gestionalec.model.Ruolo;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // WebSocket — invia messaggio in tempo reale
    @MessageMapping("/chat.invia")
    public void inviaMessaggio(@Valid @Payload MessaggioChatRequest request,
                               @AuthenticationPrincipal Utente mittente) {
        if (mittente.getRuolo() == Ruolo.AMMINISTRATORE) {
            throw new AccessDeniedException("L'amministratore non può usare la chat");
        }

        MessaggioChatResponse response = chatService.salvaEInvia(request, mittente);

        // invia in tempo reale al destinatario
        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getDestinatarioId()),
                "/queue/messaggi",
                response
        );
    }

    // REST — recupera storico messaggi
    @GetMapping("/storico/{altroUtenteId}")
    public ResponseEntity<List<MessaggioChatResponse>> storico(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long altroUtenteId) {
        return ResponseEntity.ok(chatService.storico(utente.getId(), altroUtenteId));
    }
}