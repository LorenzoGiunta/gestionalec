package com.tesi.gestionalec.facade;

import com.tesi.gestionalec.dto.request.DocumentoRequest;
import com.tesi.gestionalec.dto.response.DocumentoResponse;
import com.tesi.gestionalec.mapper.DocumentoMapper;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.TipoNotifica;
import com.tesi.gestionalec.observer.GestoreNotifiche;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import com.tesi.gestionalec.service.interfaces.DocumentoService;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoFacade {

    private final DocumentoService documentoService;
    private final PraticaService praticaService;
    private final ClienteService clienteService;
    private final GestoreNotifiche gestoreNotifiche;

    public DocumentoResponse caricaEAssegna(DocumentoRequest request, Long collaboratoreId) {
        // 1. recupera pratica e cliente
        Pratica pratica = praticaService.trovaPerId(request.getPraticaId());
        Cliente cliente = (Cliente) clienteService.trovaPerId(request.getCaricatoDaId());

        // 2. crea e salva il documento
        Documento documento = DocumentoMapper.toModel(request, pratica, cliente);
        Documento salvato = documentoService.caricaDocumento(documento);

        // 3. assegna revisore se fornito
        if (collaboratoreId != null) {
            documentoService.assegnaRevisore(salvato.getId(), collaboratoreId);
            salvato = documentoService.trovaPerId(salvato.getId());  // ricarica aggiornato
        }

        // 4. notifica il commercialista del nuovo documento
        Notifica notifica = new Notifica();
        notifica.setDestinatario(pratica.getAssegnataA());  // notifica il collaboratore assegnato
        notifica.setMessaggio("Nuovo documento caricato: " + salvato.getNome());
        notifica.setTipo(TipoNotifica.DOCUMENTO_CARICATO);
        notifica.setLetta(false);
        if (pratica.getAssegnataA() != null) {
            gestoreNotifiche.notificaTutti(notifica);
        }

        return DocumentoMapper.toResponse(salvato);
    }
}