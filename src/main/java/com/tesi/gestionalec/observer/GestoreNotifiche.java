package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GestoreNotifiche implements NotificaObservable{

    private final List<NotificaObserver> observers = new ArrayList<>();

    @Override
    public void aggiungiObeserver(NotificaObserver observer) {
        observers.add(observer);
    }

    @Override
    public void rimuoviObeserver(NotificaObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notificaTutti(Notifica notifica) {
        for (NotificaObserver observer : observers) {
            observer.aggiorna(notifica);    // notifica ognuno
        }
    }
}
