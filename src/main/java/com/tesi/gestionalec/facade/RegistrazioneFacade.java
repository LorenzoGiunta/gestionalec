package com.tesi.gestionalec.facade;

import com.tesi.gestionalec.dto.request.RegistrazioneRequest;
import com.tesi.gestionalec.dto.response.AuthResponse;
import com.tesi.gestionalec.mapper.RegistrazioneMapper;
import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.TipoNotifica;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.observer.GestoreNotifiche;
import com.tesi.gestionalec.security.GestoreTokenService;
import com.tesi.gestionalec.service.interfaces.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrazioneFacade {

    private final UtenteService utenteService;
    private final GestoreTokenService gestoreTokenService;
    private final GestoreNotifiche gestoreNotifiche;

    public AuthResponse registra(RegistrazioneRequest request) {
        Utente utente = RegistrazioneMapper.toModel(request);
        Utente salvato = utenteService.registra(utente);

        Notifica notifica = new Notifica();
        notifica.setDestinatario(salvato);
        notifica.setMessaggio("Benvenuto " + salvato.getNome() + "! Il tuo account è stato creato.");
        notifica.setTipo(TipoNotifica.CAMBIO_STATO);
        notifica.setLetta(false);
        gestoreNotifiche.notificaTutti(notifica);

        String token = gestoreTokenService.generaToken(salvato.getEmail());
        return new AuthResponse(token, salvato.getRuolo().name(), salvato.getEmail(), salvato.getNome(), salvato.getCognome());
    }
}