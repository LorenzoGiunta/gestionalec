package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Utente;

import java.util.List;

public interface AmministratoreService {
    List<Utente> trovaTuttiUtenti();
    Utente trovaPerId(Long id);
    void abilitaUtente(Long id);
    void disabilitaUtente(Long id);
    void eliminaUtente(Long id);
    void aggiornaUltimaAzione(Long amministratoreId);  // aggiorna il timestamp
}
