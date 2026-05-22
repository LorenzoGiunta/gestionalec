package com.tesi.gestionalec.state;


import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import org.springframework.stereotype.Component;

@Component
public class BozzaState implements StatoPraticaState {

    @Override
    public void avanza(Pratica pratica) {
        pratica.setStato(StatoPratica.IN_LAVORAZIONE);
        pratica.setStatoCorrente(new InLavorazioneState());
    }

    @Override
    public StatoPratica getStato() {
        return StatoPratica.BOZZA;
    }
}
