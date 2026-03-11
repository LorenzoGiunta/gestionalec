package com.tesi.gestionalec.strategy;

import org.springframework.stereotype.Component;


@Component
public class RegimeForfettarioStrategy implements TaxStrategy{

    private static final double COEFFICIENTE = 0.67;
    private static final double ALIQUOTA = 0.15;

    @Override
    public double calcola(double reddito) {
        double baseImponibile = reddito * COEFFICIENTE;
        return baseImponibile * ALIQUOTA;
    }
}
