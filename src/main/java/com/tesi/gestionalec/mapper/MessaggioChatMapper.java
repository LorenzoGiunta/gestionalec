package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.response.MessaggioChatResponse;
import com.tesi.gestionalec.model.MessaggioChat;

public class MessaggioChatMapper {

    public static MessaggioChatResponse toResponse(MessaggioChat m) {
        MessaggioChatResponse r = new MessaggioChatResponse();
        r.setId(m.getId());
        r.setMittenteNome(m.getMittente().getNome() + " " + m.getMittente().getCognome());
        r.setDestinatarioNome(m.getDestinatario().getNome() + " " + m.getDestinatario().getCognome());
        r.setTesto(m.getTesto());
        r.setLetto(m.isLetto());
        r.setDataInvio(m.getDataInvio());
        return r;
    }
}