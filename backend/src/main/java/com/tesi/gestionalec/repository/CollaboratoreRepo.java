package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Collaboratore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollaboratoreRepo extends JpaRepository<Collaboratore, Long> {

    /**
     * Cerca un Collaboratore per email.
     * Usato al momento dell'invio di un invito: se il destinatario è già
     * registrato, lo si collega subito all'invito (campo collaboratore non rimane null).
     */
    Optional<Collaboratore> findByEmail(String email);
}

