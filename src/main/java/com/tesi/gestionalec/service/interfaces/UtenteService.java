package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Utente;

import java.util.List;

public interface UtenteService {
    Utente registra(Utente utente);             // crea un nuovo utente
    Utente trovaPerId(Long id);                 // cerca per ID
    Utente trovaPerEmail(String email);         // cerca per email (usato nel login)
    List<Utente> trovaTutti();                  // solo admin
    void abilitaUtente(Long id);               // admin abilita utente
    void disabilitaUtente(Long id);            // admin disabilita utente
    void eliminaUtente(Long id);               // admin elimina utente
}
