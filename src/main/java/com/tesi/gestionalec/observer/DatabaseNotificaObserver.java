package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.repository.NotificaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseNotificaObserver implements NotificaObserver{

    private final NotificaRepository repo;

    @Override
    public void aggiorna(Notifica notifica) {
        repo.save(notifica);
    }
}
