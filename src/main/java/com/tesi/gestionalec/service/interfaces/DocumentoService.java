package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import java.util.List;

public interface DocumentoService {
    Documento caricaDocumento(Documento documento);
    Documento trovaPerId(Long id);
    List<Documento> trovaPerPratica(Pratica pratica);
    Documento nuovaVersione(Long documentoId, Documento nuovoDocumento);
    void assegnaRevisore(Long documentoId, Long collaboratoreId);
    void eliminaDocumento(Long id);      // soft delete → imposta deleted=true
}
