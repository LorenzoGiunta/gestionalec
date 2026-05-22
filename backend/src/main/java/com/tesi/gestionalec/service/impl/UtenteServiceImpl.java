package com.tesi.gestionalec.service.impl;


import com.tesi.gestionalec.exception.ResourceNotFoundException;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.interfaces.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class UtenteServiceImpl implements UtenteService, UserDetailsService {

    protected final UtenteRepo repo;
    protected final PasswordEncoder passwordEncoder;

    @Override
    public Utente registra(Utente utente) {
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        utente.setEnabled(true);
        return repo.save(utente);
    }

    @Override
    public Utente trovaPerId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "id", id));
    }

    @Override
    public Utente trovaPerEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "email", email));
    }

    @Override
    public List<Utente> trovaTutti() {
        return repo.findAll();
    }

    @Override
    public Page<Utente> trovaTutti(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public void abilitaUtente(Long id) {
        Utente utente = trovaPerId(id);
        utente.setEnabled(true);
        repo.save(utente);
    }

    @Override
    public void disabilitaUtente(Long id) {
        Utente utente = trovaPerId(id);
        utente.setEnabled(false);
        repo.save(utente);
    }

    /**
     * Soft Delete: non cancella il record dal DB.
     * Imposta deleted=true e registra il timestamp, rendendo l'utente
     * invisibile a tutte le query grazie a @SQLRestriction sull'entità.
     */
    @Override
    public void eliminaUtente(Long id) {
        Utente utente = trovaPerId(id);
        utente.setDeleted(true);
        utente.setDeletedAt(LocalDateTime.now());
        utente.setEnabled(false);   // impedisce il login anche se il filtro venisse bypassato
        repo.save(utente);
    }

    /**
     * Ripristino Soft Delete: annulla la cancellazione logica.
     * Reimposta deleted=false, azzera deletedAt e riabilita il login.
     * Necessita di una query nativa che ignori @SQLRestriction, perché
     * Hibernate altrimenti non troverebbe l'utente (deleted=true → filtrato).
     */
    @Override
    public void ripristinaUtente(Long id) {
        // findByIdIncludeDeleted bypassa @SQLRestriction tramite @Query nativa
        Utente utente = repo.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "id", id));
        utente.setDeleted(false);
        utente.setDeletedAt(null);
        utente.setEnabled(true);
        repo.save(utente);
    }

    //Spring Security chiama questo metodo automaticamente durante il login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // grazie a @SQLRestriction gli utenti con deleted=true non vengono trovati
        // → findByEmail restituirà Optional.empty() e il login fallirà con UsernameNotFoundException
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
    }

}
