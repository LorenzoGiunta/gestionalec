package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.InvitoCollaborazione;
import com.tesi.gestionalec.model.StatoInvito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvitoCollaborazioneRepo extends JpaRepository<InvitoCollaborazione, Long> {

    /** Ricerca per token UUID — usato per accettare/rifiutare dal link email */
    Optional<InvitoCollaborazione> findByToken(String token);

    /** Tutti gli inviti inviati da un commercialista (qualsiasi stato) */
    List<InvitoCollaborazione> findByCommercialista_Id(Long commercialistaId);

    /**
     * Inviti PENDING indirizzati a una specifica email.
     * Usato quando un Collaboratore appena loggato controlla gli inviti in attesa.
     */
    List<InvitoCollaborazione> findByEmailDestinatarioAndStato(String email, StatoInvito stato);

    /**
     * Verifica se esiste già un invito PENDING tra un commercialista e un'email.
     * Usato per prevenire duplicati prima di inviare un nuovo invito.
     */
    boolean existsByCommercialista_IdAndEmailDestinatarioAndStato(
            Long commercialistaId, String emailDestinatario, StatoInvito stato);

    /**
     * Inviti scaduti: ancora PENDING ma con scadeIl nel passato.
     * Usato dallo scheduler notturno per la scadenza automatica.
     */
    List<InvitoCollaborazione> findByStatoAndScadeIlBefore(StatoInvito stato, LocalDateTime soglia);

    /**
     * Recupera tutti gli inviti ACCEPTED di un commercialista, con fetch del collaboratore.
     * Evita il problema N+1 quando si mostra la lista collaboratori attivi.
     */
    @Query("""
            SELECT i FROM InvitoCollaborazione i
            JOIN FETCH i.collaboratore
            WHERE i.commercialista.id = :commId
              AND i.stato = 'ACCEPTED'
            """)
    List<InvitoCollaborazione> findCollaboratoriAttiviByCommercialista(@Param("commId") Long commId);
}
