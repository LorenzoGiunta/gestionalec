package com.tesi.gestionalec.service.impl;

import com.tesi.gestionalec.exception.ResourceNotFoundException;
import com.tesi.gestionalec.model.Amministratore;
import com.tesi.gestionalec.model.Utente;
import com.tesi.gestionalec.repository.AmministratoreRepo;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.interfaces.AmministratoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AmministratoreServiceImpl extends UtenteServiceImpl implements AmministratoreService{

    // costruttore manuale — necessario con l'ereditarietà (Lombok non gestisce super())
    public AmministratoreServiceImpl(
            UtenteRepo utenteRepository,
            PasswordEncoder passwordEncoder,
            AmministratoreRepo amministratoreRepository) {
        super(utenteRepository ,passwordEncoder);   // passa i campi al parent
        this.amministratoreRepository = amministratoreRepository;
    }

    private final AmministratoreRepo amministratoreRepository;

    @Override
    public void aggiornaUltimaAzione(Long amministratoreId) {
        Amministratore admin = amministratoreRepository.findById(amministratoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Amministratore", "id", amministratoreId));
        admin.setUltimaAzioneAmministrativa(LocalDateTime.now());
        amministratoreRepository.save(admin);
    }
}
