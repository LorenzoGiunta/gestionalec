package com.tesi.gestionalec.state;

import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;

public interface StatoPraticaState {
    void avanza(Pratica pratica);
    StatoPratica getStato();
}
