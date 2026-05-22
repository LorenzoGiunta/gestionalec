package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.request.DocumentoRequest;
import com.tesi.gestionalec.dto.response.DocumentoResponse;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Pratica;

public class DocumentoMapper {

    // Model → Response DTO
    public static DocumentoResponse toResponse(Documento documento) {
        DocumentoResponse dto = new DocumentoResponse();
        dto.setId(documento.getId());
        dto.setNome(documento.getNome());
        dto.setTipoFile(documento.getTipoFile());
        dto.setDimensione(documento.getDimensione());
        dto.setStato(documento.getStato());
        dto.setMotivazioneRifiuto(documento.getMotivazioneRifiuto());
        dto.setVersione(documento.getVersione());
        dto.setDataCaricamento(documento.getDataCaricamento());

        dto.setNomeCliente(
                documento.getCaricatoDa().getNome() + " " + documento.getCaricatoDa().getCognome()
        );

        // revisore può essere null se non ancora assegnato
        if (documento.getRevisore() != null) {
            dto.setNomeRevisore(
                    documento.getRevisore().getNome() + " " + documento.getRevisore().getCognome()
            );
        }

        return dto;
    }

    // Request DTO → Model
    public static Documento toModel(DocumentoRequest request, Pratica pratica, Cliente cliente) {
        Documento documento = new Documento();
        documento.setNome(request.getNome());
        documento.setTipoFile(request.getTipoFile());
        documento.setPercorsoFile(request.getPercorsoFile());
        documento.setDimensione(request.getDimensione());
        documento.setPratica(pratica);
        documento.setCaricatoDa(cliente);
        // stato, versione e dataCaricamento li imposta il service
        return documento;
    }
}