package com.tesi.gestionalec.service.impl;

import com.tesi.gestionalec.exception.ResourceNotFoundException;
import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoDocumento;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.DocumentoRepo;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.interfaces.CollaboratoreService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollaboratoreServiceImpl extends UtenteServiceImpl implements CollaboratoreService {

    private final CollaboratoreRepo collaboratoreRepo;
    private final DocumentoRepo documentoRepo;

    public CollaboratoreServiceImpl(
            UtenteRepo utenteRepository,
            PasswordEncoder passwordEncoder,
            CollaboratoreRepo collaboratoreRepository,
            DocumentoRepo documentoRepository) {        // ← aggiunto
        super(utenteRepository, passwordEncoder);
        this.collaboratoreRepo = collaboratoreRepository;
        this.documentoRepo = documentoRepository;       // ← aggiunto
    }


    @Override
    public List<Pratica> trovaPraticheAssegnate(Long collaboratoreId) {
        return trovaCollaboratorePerId(collaboratoreId).getPraticheAssegnate();
    }

    @Override
    public List<Documento> trovaDocumentiInRevisione(Long collaboratoreId) {
        return trovaCollaboratorePerId(collaboratoreId).getDocumentiInRevisione();
    }

    @Override
    public void approvaDocumento(Long documentoId) {
        Documento doc = documentoRepo.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", "id", documentoId));
        doc.setStato(StatoDocumento.APPROVATO);
        documentoRepo.save(doc);
    }

    @Override
    public void rifiutaDocumento(Long documentoId, String motivazione) {
        Documento doc = documentoRepo.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", "id", documentoId));
        doc.setStato(StatoDocumento.RIFIUTATO);
        doc.setMotivazioneRifiuto(motivazione);
        documentoRepo.save(doc);
    }

    private Collaboratore trovaCollaboratorePerId(Long id) {
        return collaboratoreRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboratore", "id", id));
    }
}