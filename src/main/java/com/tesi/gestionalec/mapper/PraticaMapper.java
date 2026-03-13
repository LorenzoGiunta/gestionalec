package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.request.PraticaRequest;
import com.tesi.gestionalec.dto.response.PraticaResponse;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.Cliente;


public class PraticaMapper {
    // Model → Response DTO
    public static PraticaResponse toResponse(Pratica pratica) {
        PraticaResponse dto = new PraticaResponse();
        dto.setId(pratica.getId());
        dto.setTipoPratica(pratica.getTipoPratica());
        dto.setStato(pratica.getStato());
        dto.setDataCreazione(pratica.getDataCreazione());

        // dal model Cliente prendi solo il nome — eviti di esporre tutto l'oggetto
        dto.setNomeCliente(
                pratica.getCliente().getNome() + " " + pratica.getCliente().getCognome()
        );

        // il collaboratore può essere null se non ancora assegnato
        if (pratica.getAssegnataA() != null) {
            dto.setNomeCollaboratore(
                    pratica.getAssegnataA().getNome() + " " + pratica.getAssegnataA().getCognome()
            );
        }

        return dto;
    }

    // Request DTO → Model
    public static Pratica toModel(PraticaRequest request, Cliente cliente) {
        Pratica pratica = new Pratica();
        pratica.setCliente(cliente);
        pratica.setTipoPratica(request.getTipoPratica());
        return pratica;
        // stato e dataCreazione li imposta il service/JPA — non il mapper
    }
}
