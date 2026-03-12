package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.response.UtenteResponse;
import com.tesi.gestionalec.model.Utente;

public class UtetenteMapper {

    public static UtenteResponse ToResponse(Utente u){
        UtenteResponse dto = new UtenteResponse();
        dto.setId(u.getId());
        dto.setNome(u.getNome());
        dto.setCognome(u.getCognome());
        dto.setEmail(u.getEmail());
        dto.setRuolo(u.getRuolo());
        dto.setEnabled(u.isEnabled());
        return dto;
    }
}
