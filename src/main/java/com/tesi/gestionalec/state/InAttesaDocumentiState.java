package com.tesi.gestionalec.state;

import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import org.springframework.stereotype.Component;

@Component
public class InAttesaDocumentiState implements StatoPraticaState{


    @Override
    public void avanza(Pratica pratica) {
        pratica.setStato(StatoPratica.COMPLETATA);
        pratica.setStatoCorrente(new CompletataState());
    }

    @Override
    public StatoPratica getStato() {
        return StatoPratica.IN_ATTESA_DOCUMENTI;
    }
}
