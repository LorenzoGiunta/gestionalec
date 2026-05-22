package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.dto.request.MessaggioChatRequest;
import com.tesi.gestionalec.dto.response.MessaggioChatResponse;
import com.tesi.gestionalec.model.Utente;

import java.util.List;

public interface ChatService {
    MessaggioChatResponse salvaEInvia(MessaggioChatRequest request, Utente mittente);
    List<MessaggioChatResponse> storico(Long utenteAId, Long utenteBId);
}