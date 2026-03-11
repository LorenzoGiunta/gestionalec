package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;

import java.util.List;

public interface ClienteService {
    Cliente trovaPerId(Long id);
    Cliente trovaPerEmail(String email);
    Cliente trovaPerCodFiscale(String codFiscale);
    List<Pratica> trovaPratiche(Long clienteId);
    List<Documento> trovaDocumenti(Long clienteId);
    Cliente aggiorna(Long id, Cliente clienteAggiornato);  // aggiorna dati personali
}
