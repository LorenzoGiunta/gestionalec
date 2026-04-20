package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.MessaggioChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessaggioChatRepo extends JpaRepository<MessaggioChat, Long> {

    // tutti i messaggi tra due utenti ordinati per data
    @Query("""
        SELECT m FROM MessaggioChat m
        WHERE (m.mittente.id = :utenteA AND m.destinatario.id = :utenteB)
           OR (m.mittente.id = :utenteB AND m.destinatario.id = :utenteA)
        ORDER BY m.dataInvio ASC
    """)
    List<MessaggioChat> trovaStotico(Long utenteA, Long utenteB);
}