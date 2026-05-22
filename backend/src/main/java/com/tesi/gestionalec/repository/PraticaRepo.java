package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PraticaRepo extends JpaRepository<Pratica, Long> {
    List<Pratica> findByCliente(Cliente cliente);           // pratiche di un cliente
    List<Pratica> findByAssegnataA(Collaboratore c);        // pratiche di un collaboratore
    List<Pratica> findByStato(StatoPratica stato);          // pratiche per stato
    Page<Pratica> findAll(Pageable pageable);               // tutte, con paginazione
}
