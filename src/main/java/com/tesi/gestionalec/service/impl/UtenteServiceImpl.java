package com.tesi.gestionalec.service.impl;


import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.interfaces.UtenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + id));
    }

    @Override
    public Utente trovaPerEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));
    }

    @Override
    public List<Utente> trovaTutti() {
        return repo.findAll();
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

    @Override
    public void eliminaUtente(Long id) {
        trovaPerId(id);
        repo.deleteById(id);
    }

    //Spring Security chiama questo metodo automaticamente durante il login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
    }

}
