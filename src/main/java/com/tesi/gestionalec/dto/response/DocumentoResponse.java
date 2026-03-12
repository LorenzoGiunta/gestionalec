package com.tesi.gestionalec.dto.response;

import com.tesi.gestionalec.model.StatoDocumento;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentoResponse {
    private Long id;
    private String nome;
    private String tipoFile;
    private Long dimensione;
    private StatoDocumento stato;
    private String motivazioneRifiuto;
    private Integer versione;
    private LocalDateTime dataCaricamento;
    private String nomeCliente;
    private String nomeRevisore;    // null se non ancora assegnato
}
