package com.tesi.gestionalec.service.impl;


import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.repository.ClienteRepo;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl extends UtenteServiceImpl implements ClienteService {

    private final ClienteRepo clienteRepository;

    public ClienteServiceImpl(
            UtenteRepo utenteRepository,
            PasswordEncoder passwordEncoder,
            ClienteRepo clienteRepository) {
        super(utenteRepository,passwordEncoder);
        this.clienteRepository = clienteRepository;
    }



    @Override
    public Cliente trovaPerCodFiscale(String codFiscale) {
        return clienteRepository.findByCodFiscale(codFiscale)
                .orElseThrow(() -> new RuntimeException("Cliente non trovato con CF: " + codFiscale));
    }

    @Override
    public List<Pratica> trovaPratiche(Long clienteId) {
        Cliente cliente = trovaClientePerId(clienteId);
        return cliente.getPratiche();
    }

    @Override
    public List<Documento> trovaDocumenti(Long clienteId) {
        Cliente cliente = trovaClientePerId(clienteId);
        return cliente.getDocumenti();
    }

    @Override
    public Cliente aggiorna(Long id, Cliente dati) {
        Cliente cliente = trovaClientePerId(id);
        cliente.setNome(dati.getNome());
        cliente.setCognome(dati.getCognome());
        cliente.setEmail(dati.getEmail());
        cliente.setCodFiscale(dati.getCodFiscale());
        cliente.setPIVA(dati.getPIVA());
        cliente.setRegime(dati.getRegime());
        cliente.setRedditoAnnuo(dati.getRedditoAnnuo());
        return clienteRepository.save(cliente);
    }

    private Cliente trovaClientePerId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente non trovato con id: " + id));
    }
}
