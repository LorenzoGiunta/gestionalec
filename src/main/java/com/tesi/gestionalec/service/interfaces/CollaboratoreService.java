package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;

import java.util.List;

public interface CollaboratoreService extends UtenteService{
    List<Pratica> trovaPraticheAssegnate(Long collaboratoreId);
    List<Documento> trovaDocumentiInRevisione(Long collaboratoreId);
    void approvaDocumento(Long documentoId);
    void rifiutaDocumento(Long documentoId, String motivazione);
}
