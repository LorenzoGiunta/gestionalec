package com.tesi.gestionalec.state;

import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompletataState implements StatoPraticaState{
    @Override
    public void avanza(Pratica pratica) {
        throw new IllegalStateException("La pratica è stata completatta");
    }

    @Override
    public StatoPratica getStato() {
        return StatoPratica.COMPLETATA;
    }
}
