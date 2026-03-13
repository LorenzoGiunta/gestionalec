package com.tesi.gestionalec.dto.request;

import com.tesi.gestionalec.model.TipoPratica;
import lombok.Data;

@Data
public class PraticaRequest {
    private Long clienteId;
    private TipoPratica tipoPratica;
}
