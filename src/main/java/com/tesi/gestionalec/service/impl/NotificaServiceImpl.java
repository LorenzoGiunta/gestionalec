package com.tesi.gestionalec.service.impl;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.repository.NotificaRepo;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.interfaces.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificaServiceImpl implements NotificaService {


    private final NotificaRepo notificaRepo;
    private final UtenteRepo utenteRepository;

    @Override
    public List<Notifica> trovaPerUtente(Utente utente) {
        return notificaRepo.findByDestinatario(utente);
    }

    @Override
    public List<Notifica> trovaNonLette(Utente utente) {
        return notificaRepo.findByDestinatarioAndLettaFalse(utente);
    }

    @Override
    public void segnaComeLetta(Long notificaId) {
        Notifica notifica = notificaRepo.findById(notificaId)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata con id: " + notificaId));

        notifica.setLetta(true);
        notificaRepo.save(notifica);
    }

    @Override
    public void segnaComeLetteTutte(Long utenteId) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + utenteId));

        List<Notifica> nonLette = notificaRepo.findByDestinatarioAndLettaFalse(utente);
        nonLette.forEach(n -> n.setLetta(true));
        notificaRepo.saveAll(nonLette);
    }
}
