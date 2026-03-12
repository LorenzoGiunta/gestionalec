package com.tesi.gestionalec.observer.interfaces;

import com.tesi.gestionalec.model.Notifica;

public interface NotificaObservable {
    void aggiungiObeserver(NotificaObserver observer);
    void rimuoviObeserver(NotificaObserver observer);
    void notificaTutti(Notifica notifica);
}
