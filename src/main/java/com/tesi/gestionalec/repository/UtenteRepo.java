package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtenteRepo extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);
}
