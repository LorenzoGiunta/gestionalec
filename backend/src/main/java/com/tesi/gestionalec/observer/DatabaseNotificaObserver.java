package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.observer.interfaces.NotificaObserver;
import com.tesi.gestionalec.repository.NotificaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseNotificaObserver implements NotificaObserver {

    private final NotificaRepo repo;

    /**
     * Salvataggio asincrono della notifica nel DB.
     * Gira su un thread del pool "notificaExecutor" → non blocca il thread HTTP.
     * @Async funziona perché viene chiamato dall'esterno del bean (via proxy Spring AOP).
     */
    @Override
    @Async("notificaExecutor")
    public void aggiorna(Notifica notifica) {
        repo.save(notifica);
    }
}
