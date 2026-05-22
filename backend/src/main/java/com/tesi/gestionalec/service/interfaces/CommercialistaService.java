package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Commercialista;
import com.tesi.gestionalec.model.Pratica;
import java.util.List;

public interface CommercialistaService extends UtenteService{
    List<Pratica> trovaTutteLePratiche();
    void assegnaCollaboratore(Long praticaId, Long collaboratoreId);
    void avanzaStatoPratica(Long praticaId);
    double calcolaImposteCliente(Long clienteId);
}