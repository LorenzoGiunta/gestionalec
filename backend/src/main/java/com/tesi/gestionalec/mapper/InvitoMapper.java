package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.response.InvitoResponse;
import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.InvitoCollaborazione;

/**
 * Mapper statico per InvitoCollaborazione → InvitoResponse.
 * Segue lo stesso stile degli altri mapper del progetto (es. PraticaMapper, NotificaMapper).
 */
public class InvitoMapper {

    private InvitoMapper() {
        // utility class — non istanziare
    }

    public static InvitoResponse toResponse(InvitoCollaborazione invito) {
        Collaboratore collab = invito.getCollaboratore();

        return InvitoResponse.builder()
                .id(invito.getId())
                .token(invito.getToken())
                .emailDestinatario(invito.getEmailDestinatario())
                // dati commercialista mittente
                .commercialistaId(invito.getCommercialista().getId())
                .nomeCommercialista(
                        invito.getCommercialista().getNome() + " " + invito.getCommercialista().getCognome()
                )
                .studioCommercialista(invito.getCommercialista().getNumeroAlbo())
                // dati collaboratore — null-safe: il collab potrebbe non essere ancora registrato
                .collaboratoreId(collab != null ? collab.getId() : null)
                .nomeCollaboratore(collab != null
                        ? collab.getNome() + " " + collab.getCognome()
                        : null)
                .stato(invito.getStato())
                .creatoIl(invito.getCreatoIl())
                .scadeIl(invito.getScadeIl())
                .build();
    }
}
