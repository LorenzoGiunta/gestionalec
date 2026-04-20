package com.tesi.gestionalec.service.impl;

import com.tesi.gestionalec.dto.request.MessaggioChatRequest;
import com.tesi.gestionalec.dto.response.MessaggioChatResponse;
import com.tesi.gestionalec.mapper.MessaggioChatMapper;
import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.repository.MessaggioChatRepo;
import com.tesi.gestionalec.service.interfaces.ChatService;
import com.tesi.gestionalec.service.interfaces.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessaggioChatRepo repo;
    private final UtenteService utenteService;

    @Override
    public MessaggioChatResponse salvaEInvia(MessaggioChatRequest request, Utente mittente) {
        Utente destinatario = utenteService.trovaPerId(request.getDestinatarioId());

        if (!combinazioneValida(mittente.getRuolo(), destinatario.getRuolo())) {
            throw new AccessDeniedException("Non puoi inviare messaggi a questo utente");
        }

        MessaggioChat messaggio = new MessaggioChat();
        messaggio.setMittente(mittente);
        messaggio.setDestinatario(destinatario);
        messaggio.setTesto(request.getTesto());
        messaggio.setLetto(false);

        return MessaggioChatMapper.toResponse(repo.save(messaggio));
    }

    @Override
    public List<MessaggioChatResponse> storico(Long utenteAId, Long utenteBId) {
        return repo.trovaStotico(utenteAId, utenteBId)
                .stream()
                .map(MessaggioChatMapper::toResponse)
                .toList();
    }

    private boolean combinazioneValida(Ruolo mittente, Ruolo destinatario) {
        return switch (mittente) {
            case CLIENTE        -> destinatario == Ruolo.COMMERCIALISTA || destinatario == Ruolo.COLLABORATORE;
            case COMMERCIALISTA -> destinatario == Ruolo.CLIENTE        || destinatario == Ruolo.COLLABORATORE;
            case COLLABORATORE  -> destinatario == Ruolo.CLIENTE        || destinatario == Ruolo.COMMERCIALISTA;
            case AMMINISTRATORE -> false;
        };
    }
}