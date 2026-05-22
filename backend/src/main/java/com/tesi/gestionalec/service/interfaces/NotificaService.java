package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificaService {
    List<Notifica> trovaPerUtente(Utente utente);
    Page<Notifica> trovaPerUtente(Utente utente, Pageable pageable);   // ← paginata
    List<Notifica> trovaNonLette(Utente utente);
    Page<Notifica> trovaNonLette(Utente utente, Pageable pageable);   // ← paginata
    void segnaComeLetta(Long notificaId);
    void segnaComeLetteTutte(Long utenteId);
}