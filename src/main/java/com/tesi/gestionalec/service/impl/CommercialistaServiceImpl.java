package com.tesi.gestionalec.service.impl;
import com.tesi.gestionalec.exception.ResourceNotFoundException;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.repository.*;
import com.tesi.gestionalec.service.interfaces.CalcoloImposteService;
import com.tesi.gestionalec.service.interfaces.CommercialistaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class CommercialistaServiceImpl extends UtenteServiceImpl implements CommercialistaService {

    private final PraticaRepo praticaRepository;
    private final CollaboratoreRepo collaboratoreRepository;
    private final ClienteRepo clienteRepo;
    private final CalcoloImposteService calcoloImposte;


    public CommercialistaServiceImpl(
            UtenteRepo utenteRepository,
            PasswordEncoder passwordEncoder,
            PraticaRepo praticaRepository,
            CollaboratoreRepo collaboratoreRepository,
            ClienteRepo clienteRepo,
            CalcoloImposteService calcoloImposte) {
        super(utenteRepository, passwordEncoder);
        this.praticaRepository = praticaRepository;
        this.collaboratoreRepository = collaboratoreRepository;
        this.clienteRepo = clienteRepo;
        this.calcoloImposte = calcoloImposte;
    }

    @Override
    public List<Pratica> trovaTutteLePratiche() {
        return praticaRepository.findAll();
    }

    @Override
    public void assegnaCollaboratore(Long praticaId, Long collaboratoreId) {
        Pratica pratica = trovaById(praticaId);

        Collaboratore collaboratore = collaboratoreRepository.findById(collaboratoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboratore", "id", collaboratoreId));

        pratica.setAssegnataA(collaboratore);
        praticaRepository.save(pratica);
    }

    @Override
    public void avanzaStatoPratica(Long praticaId) {
        Pratica pratica = trovaById(praticaId);

        pratica.getStatoCorrente().avanza(pratica);   // delega allo stato corrente
        praticaRepository.save(pratica);
    }

    @Override
    public double calcolaImposteCliente(Long clienteId) {
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));
        return calcoloImposte.CalcolaPerCliente(cliente);
    }

    private Pratica trovaById(Long id){
        return praticaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pratica", "id", id));
    }
}
