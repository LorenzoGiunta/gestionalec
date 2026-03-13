package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaRepo extends JpaRepository<Notifica, Long> {
    List<Notifica> findByDestinatario(Utente utente);
    List<Notifica> findByDestinatarioAndLettaFalse(Utente utente);
}
