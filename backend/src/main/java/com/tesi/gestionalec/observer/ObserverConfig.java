package com.tesi.gestionalec.observer;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ObserverConfig {

    private final GestoreNotifiche gestN;
    private final DatabaseNotificaObserver DBN;
    private final EmailNotificaObserver email;

    @PostConstruct
    public void registraObserver (){
        gestN.aggiungiObeserver(DBN);
        gestN.aggiungiObeserver(email);
    }
}