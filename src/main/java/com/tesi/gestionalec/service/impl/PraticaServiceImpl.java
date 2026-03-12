package com.tesi.gestionalec.service.impl;


import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.observer.GestoreNotifiche;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.PraticaRepo;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PraticaServiceImpl implements PraticaService {

    private final PraticaRepo praticaRepo;
    private final CollaboratoreRepo collaboratoreRepo;
    private final GestoreNotifiche gestoreNotifiche;



    @Override
    public Pratica creaPratica(Pratica pratica) {

        pratica.setStato(StatoPratica.BOZZA);
        Pratica salvata = praticaRepo.save(pratica);

        // notifica il cliente che la pratica è stata creata
        Notifica notifica = new Notifica();
        notifica.setDestinatario(pratica.getCliente());
        notifica.setMessaggio("La tua pratica è stata creata con stato: " + pratica.getStato());
        notifica.setTipo(TipoNotifica.CAMBIO_STATO);
        notifica.setLetta(false);

        // Tutti = tutti gli observer
        gestoreNotifiche.notificaTutti(notifica);

        return salvata;
    }

    @Override
    public Pratica trovaPerId(Long id) {
        return praticaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pratica non trovata con id: " + id));
    }

    @Override
    public List<Pratica> trovaTutte() {
        return praticaRepo.findAll();
    }

    @Override
    public List<Pratica> trovaPerCliente(Cliente cliente) {
        return praticaRepo.findByCliente(cliente);
    }

    @Override
    public List<Pratica> trovaPerCollaboratore(Collaboratore collaboratore) {
        return praticaRepo.findByAssegnataA(collaboratore);
    }

    @Override
    public void avanzaStato(Long praticaId) {

        Pratica pratica = trovaPerId(praticaId);

        // pattern State
        pratica.getStatoCorrente().avanza(pratica);
        praticaRepo.save(pratica);

        // notifica il cliente del cambio stato
        Notifica notifica = new Notifica();
        notifica.setDestinatario(pratica.getCliente());
        notifica.setMessaggio("La tua pratica è passata allo stato: " + pratica.getStato());
        notifica.setTipo(TipoNotifica.CAMBIO_STATO);
        notifica.setLetta(false);
        gestoreNotifiche.notificaTutti(notifica);
    }

    @Override
    public void assegnaCollaboratore(Long praticaId, Long collaboratoreId) {

        Pratica pratica = trovaPerId(praticaId);

        Collaboratore collaboratore = collaboratoreRepo.findById(collaboratoreId)
                .orElseThrow(() -> new RuntimeException("Collaboratore non trovato con id: " + collaboratoreId));

        pratica.setAssegnataA(collaboratore);
        praticaRepo.save(pratica);

        // notifica il collaboratore dell'assegnazione
        Notifica notifica = new Notifica();
        notifica.setDestinatario(collaboratore);
        notifica.setMessaggio("Ti è stata assegnata una nuova pratica con id: " + praticaId);
        notifica.setTipo(TipoNotifica.CAMBIO_STATO);
        notifica.setLetta(false);
        gestoreNotifiche.notificaTutti(notifica);
    }

    @Override
    public List<Pratica> trovaPerStato(StatoPratica stato) {
        return praticaRepo.findByStato(stato);
    }
}
