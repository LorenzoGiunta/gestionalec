package com.tesi.gestionalec.dto.response;

import com.tesi.gestionalec.model.StatoPratica;
import com.tesi.gestionalec.model.TipoPratica;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PraticaResponse {
    private Long id;
    private TipoPratica tipoPratica;
    private StatoPratica stato;
    private LocalDateTime dataCreazione;
    private LocalDate scadenza;
    private String nomeCliente;         // solo il nome, non tutto l'oggetto Cliente
    private String nomeCollaboratore;   // null se non ancora assegnata
}