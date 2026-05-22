package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UtenteRepo extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    /**
     * Cerca un utente per ID ignorando il filtro @SQLRestriction (deleted=false).
     * Serve SOLO per ripristinare un utente che è stato soft-deleted
     * (deleted=true → Hibernate lo nasconde a tutte le query normali).
     * La query nativa accede direttamente alla tabella utente senza il filtro.
     */
    @Query(value = "SELECT * FROM utente WHERE id = :id", nativeQuery = true)
    Optional<Utente> findByIdIncludeDeleted(@Param("id") Long id);
}
