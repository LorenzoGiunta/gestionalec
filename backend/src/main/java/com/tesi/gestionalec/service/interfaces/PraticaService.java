package com.tesi.gestionalec.service.interfaces;

import com.tesi.gestionalec.model.Collaboratore;
import com.tesi.gestionalec.model.Cliente;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PraticaService {
    Pratica creaPratica(Pratica pratica);
    Pratica trovaPerId(Long id);
    List<Pratica> trovaTutte();
    Page<Pratica> trovaTutte(Pageable pageable);              // ← paginata
    List<Pratica> trovaPerCliente(Cliente cliente);
    List<Pratica> trovaPerCollaboratore(Collaboratore collaboratore);
    void avanzaStato(Long praticaId);
    void assegnaCollaboratore(Long praticaId, Long collaboratoreId);
    List<Pratica> trovaPerStato(StatoPratica stato);
    void eliminaPratica(Long id);        // soft delete → imposta deleted=true
}