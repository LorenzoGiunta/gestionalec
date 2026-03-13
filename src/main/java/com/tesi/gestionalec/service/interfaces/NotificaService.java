package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.Utente;
import java.util.List;

public interface NotificaService {
    List<Notifica> trovaPerUtente(Utente utente);
    List<Notifica> trovaNonLette(Utente utente);
    void segnaComeLetta(Long notificaId);
    void segnaComeLetteTutte(Long utenteId);
}