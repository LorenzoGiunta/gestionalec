package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.response.NotificaResponse;
import com.tesi.gestionalec.model.Notifica;

public class NotificaMapper {

    // Model → Response DTO
    public static NotificaResponse toResponse(Notifica notifica) {
        NotificaResponse dto = new NotificaResponse();
        dto.setId(notifica.getId());
        dto.setMessaggio(notifica.getMessaggio());
        dto.setTipo(notifica.getTipo());
        dto.setLetta(notifica.isLetta());
        dto.setDataCreazione(notifica.getDataCreazione());
        return dto;
    }
}
