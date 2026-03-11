package com.tesi.gestionalec.strategy;


import org.springframework.stereotype.Component;

@Component
public class RegimeOrdinarioStrategy implements TaxStrategy{

    @Override
    public double calcola(double reddito) {
        double imposta = 0;

        if (reddito <= 28000) {
            imposta = reddito * 0.23;
        } else if (reddito <= 50000) {
            imposta = 28000 * 0.23 + (reddito - 28000) * 0.35;
        } else {
            imposta = 28000 * 0.23 + 22000 * 0.35 + (reddito - 50000) * 0.43;
        }

        return imposta;
    }
}
