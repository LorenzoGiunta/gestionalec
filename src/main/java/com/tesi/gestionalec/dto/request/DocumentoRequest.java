package com.tesi.gestionalec.dto.request;

import lombok.Data;

@Data
public class DocumentoRequest {
    private String nome;
    private String tipoFile;
    private String percorsoFile;
    private Long dimensione;
    private Long praticaId;
    private Long caricatoDaId;
}