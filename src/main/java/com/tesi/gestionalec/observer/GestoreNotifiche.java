package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.observer.interfaces.NotificaObservable;
import com.tesi.gestionalec.observer.interfaces.NotificaObserver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GestoreNotifiche implements NotificaObservable {

    private final List<NotificaObserver> observers = new ArrayList<>();

    @Override
    public void aggiungiObeserver(NotificaObserver observer) {
        observers.add(observer);
    }

    @Override
    public void rimuoviObeserver(NotificaObserver observer) {
        observers.remove(observer);
    }

    /**
     * Propaga la notifica a tutti gli observer registrati.
     *
     * Il ciclo for ritorna quasi istantaneamente: ogni observer ha il proprio
     * metodo aggiorna() annotato con @Async("notificaExecutor"), quindi Spring
     * smista il lavoro su thread del pool e libera subito il thread HTTP.
     *
     * Flusso risultante:
     *   Thread HTTP  →  notificaTutti()  →  [dispatcha su notifica-thread-X]
     *                                     ↘  DatabaseNotificaObserver.aggiorna()  (async)
     *                                     ↘  EmailNotificaObserver.aggiorna()     (async)
     *   Thread HTTP ritorna alla risposta REST immediatamente ↑
     */
    @Override
    public void notificaTutti(Notifica notifica) {
        for (NotificaObserver observer : observers) {
            observer.aggiorna(notifica);    // dispatcha su thread asincrono
        }
    }
}
