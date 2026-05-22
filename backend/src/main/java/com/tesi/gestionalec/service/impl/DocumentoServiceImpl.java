package com.tesi.gestionalec.service.impl;


import com.tesi.gestionalec.exception.ResourceNotFoundException;
import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoDocumento;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.DocumentoRepo;
import com.tesi.gestionalec.service.interfaces.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoServiceImpl implements DocumentoService {


    private final DocumentoRepo documentoRepository;
    private final CollaboratoreRepo collaboratoreRepository;


    @Override
    public Documento caricaDocumento(Documento documento) {
        documento.setVersione(1);
        documento.setStato(StatoDocumento.IN_REVISIONE);
        return documentoRepository.save(documento);
    }

    @Override
    public Documento trovaPerId(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", "id", id));
    }

    @Override
    public List<Documento> trovaPerPratica(Pratica pratica) {
        return documentoRepository.findByPratica(pratica);
    }

    @Override
    public Documento nuovaVersione(Long documentoId, Documento nuovoDocumento) {
        Documento vecchio = trovaPerId(documentoId);
        nuovoDocumento.setVersione(vecchio.getVersione() + 1);  // incrementa versione
        nuovoDocumento.setStato(StatoDocumento.IN_REVISIONE);    // riparte da IN_REVISIONE
        nuovoDocumento.setPratica(vecchio.getPratica());         // stessa pratica
        nuovoDocumento.setCaricatoDa(vecchio.getCaricatoDa());   // stesso cliente
        return documentoRepository.save(nuovoDocumento);
    }

    @Override
    public void assegnaRevisore(Long documentoId, Long collaboratoreId) {
        Documento documento = trovaPerId(documentoId);
        Collaboratore collaboratore = collaboratoreRepository.findById(collaboratoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboratore", "id", collaboratoreId));
        documento.setRevisore(collaboratore);
        documentoRepository.save(documento);
    }

    /**
     * Soft Delete: non cancella il file né il record dal DB.
     * Imposta deleted=true e registra il timestamp. Il documento diventa
     * invisibile a tutte le query grazie a @SQLRestriction sull'entità,
     * ma il file fisico e i metadati sono preservati per audit fiscale.
     */
    @Override
    public void eliminaDocumento(Long id) {
        Documento documento = trovaPerId(id);
        documento.setDeleted(true);
        documento.setDeletedAt(LocalDateTime.now());
        documentoRepository.save(documento);
    }

}
