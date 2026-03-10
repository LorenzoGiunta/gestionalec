package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Documento;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepo extends JpaRepository<Documento, Long> {
    List<Documento> findByPratica(Pratica pratica);         // documenti di una pratica
    List<Documento> findByStato(StatoDocumento stato);      // documenti per stato
    List<Documento> findByCaricatoDa(Cliente cliente);      // documenti di un cliente
}
