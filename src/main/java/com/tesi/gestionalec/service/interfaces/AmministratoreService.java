package com.tesi.gestionalec.service.interfaces;


public interface AmministratoreService extends UtenteService{
    void aggiornaUltimaAzione(Long amministratoreId);  // aggiorna il timestamp
}
