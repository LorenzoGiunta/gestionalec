package com.tesi.gestionalec.facade;

import com.tesi.gestionalec.dto.request.PraticaRequest;
import com.tesi.gestionalec.dto.response.PraticaResponse;
import com.tesi.gestionalec.mapper.PraticaMapper;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.service.interfaces.ClienteService;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PraticaFacade {

    private final PraticaService praticaService;
    private final ClienteService clienteService;

    public PraticaResponse creaEAssegna(PraticaRequest request, Long collaboratoreId) {
        // 1. recupera il cliente
        Cliente cliente = (Cliente) clienteService.trovaPerId(request.getClienteId());

        // 2. crea la pratica (dentro notifica il cliente — pattern Observer)
        Pratica pratica = PraticaMapper.toModel(request, cliente);
        Pratica salvata = praticaService.creaPratica(pratica);

        // 3. se è già noto il collaboratore, assegna subito
        if (collaboratoreId != null) {
            praticaService.assegnaCollaboratore(salvata.getId(), collaboratoreId);
            salvata = praticaService.trovaPerId(salvata.getId());  // ricarica aggiornata
        }

        return PraticaMapper.toResponse(salvata);
    }

    public PraticaResponse avanzaERecupera(Long praticaId) {
        // 1. avanza stato (dentro notifica il cliente — pattern Observer)
        praticaService.avanzaStato(praticaId);

        // 2. restituisce la pratica aggiornata
        return PraticaMapper.toResponse(praticaService.trovaPerId(praticaId));
    }
}