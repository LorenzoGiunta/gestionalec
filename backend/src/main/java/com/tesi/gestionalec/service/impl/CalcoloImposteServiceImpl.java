package com.tesi.gestionalec.service.impl;

import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.service.interfaces.CalcoloImposteService;
import com.tesi.gestionalec.strategy.RegimeForfettarioStrategy;
import com.tesi.gestionalec.strategy.RegimeOrdinarioStrategy;
import com.tesi.gestionalec.strategy.TaxStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalcoloImposteServiceImpl implements CalcoloImposteService {

    private final RegimeOrdinarioStrategy ordinario;
    private final RegimeForfettarioStrategy forfettario;

    @Override
    public double CalcolaPerCliente(Cliente cliente) {
        TaxStrategy strategia =
                switch (cliente.getRegime()) {
                    case FORFETTARIO -> forfettario;
                    case ORDINARIO -> ordinario;
                };
        return strategia.calcola(cliente.getRedditoAnnuo());
    }
}
