package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.repository.NotificaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseNotificaObserver implements NotificaObserver{


    private final NotificaRepo repo;

    @Override
    public void aggiorna(Notifica notifica) {
        repo.save(notifica);
    }
}
