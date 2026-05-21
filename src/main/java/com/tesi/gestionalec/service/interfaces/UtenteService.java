package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UtenteService {
    Utente registra(Utente utente);             // crea un nuovo utente
    Utente trovaPerId(Long id);                 // cerca per ID
    Utente trovaPerEmail(String email);         // cerca per email (usato nel login)
    List<Utente> trovaTutti();                  // solo admin
    Page<Utente> trovaTutti(Pageable pageable); // solo admin — paginata
    void abilitaUtente(Long id);               // admin abilita utente
    void disabilitaUtente(Long id);            // admin disabilita utente
    void eliminaUtente(Long id);               // soft delete — imposta deleted=true
    void ripristinaUtente(Long id);            // annulla la soft delete
}
