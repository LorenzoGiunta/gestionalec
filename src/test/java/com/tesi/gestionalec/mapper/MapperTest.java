package com.tesi.gestionalec.mapper;

import com.tesi.gestionalec.dto.request.DocumentoRequest;
import com.tesi.gestionalec.dto.request.PraticaRequest;
import com.tesi.gestionalec.dto.response.*;
import com.tesi.gestionalec.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Test unitari per tutti i Mapper.
 * Test puri: nessuna dipendenza Spring o DB — solo logica di mapping.
 */
@DisplayName("Mappers – Unit Tests")
class MapperTest {

    // ═══════════════════════════════════════════════════════════════════════
    // PraticaMapper
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("PraticaMapper.toResponse: mappa tutti i campi base correttamente")
    void praticaMapper_toResponse_campiBase() {
        Cliente cliente = new Cliente();
        cliente.setNome("Mario");
        cliente.setCognome("Rossi");

        Pratica pratica = new Pratica();
        pratica.setId(1L);
        pratica.setTipoPratica(TipoPratica.DICHIARAZIONE_REDDITI);
        pratica.setStato(StatoPratica.BOZZA);
        pratica.setCliente(cliente);
        pratica.setScadenza(LocalDate.of(2024, 12, 31));

        PraticaResponse dto = PraticaMapper.toResponse(pratica);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTipoPratica()).isEqualTo(TipoPratica.DICHIARAZIONE_REDDITI);
        assertThat(dto.getStato()).isEqualTo(StatoPratica.BOZZA);
        assertThat(dto.getNomeCliente()).isEqualTo("Mario Rossi");
        assertThat(dto.getScadenza()).isEqualTo(LocalDate.of(2024, 12, 31));
    }

    @Test
    @DisplayName("PraticaMapper.toResponse: collaboratore null → nomeCollaboratore null")
    void praticaMapper_toResponse_senzaCollaboratore() {
        Cliente c = clienteCon("Anna", "Verdi");
        Pratica pratica = praticaCon(c, null);

        PraticaResponse dto = PraticaMapper.toResponse(pratica);

        assertThat(dto.getNomeCollaboratore()).isNull();
    }

    @Test
    @DisplayName("PraticaMapper.toResponse: collaboratore presente → nomeCollaboratore settato")
    void praticaMapper_toResponse_conCollaboratore() {
        Cliente c = clienteCon("Anna", "Verdi");
        Collaboratore col = new Collaboratore();
        col.setNome("Luca");
        col.setCognome("Bianchi");

        Pratica pratica = praticaCon(c, col);
        PraticaResponse dto = PraticaMapper.toResponse(pratica);

        assertThat(dto.getNomeCollaboratore()).isEqualTo("Luca Bianchi");
    }

    @Test
    @DisplayName("PraticaMapper.toModel: mappa tipoPratica e cliente correttamente")
    void praticaMapper_toModel() {
        Cliente c = clienteCon("Mario", "Rossi");
        PraticaRequest request = new PraticaRequest();
        request.setTipoPratica(TipoPratica.IVA);
        request.setScadenza(LocalDate.of(2024, 6, 30));

        Pratica pratica = PraticaMapper.toModel(request, c);

        assertThat(pratica.getCliente()).isEqualTo(c);
        assertThat(pratica.getTipoPratica()).isEqualTo(TipoPratica.IVA);
        assertThat(pratica.getScadenza()).isEqualTo(LocalDate.of(2024, 6, 30));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DocumentoMapper
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("DocumentoMapper.toResponse: mappa tutti i campi correttamente")
    void documentoMapper_toResponse_campiBase() {
        Cliente cliente = clienteCon("Mario", "Rossi");
        Documento doc = new Documento();
        doc.setId(10L);
        doc.setNome("CUD_2024.pdf");
        doc.setTipoFile("CUD");
        doc.setDimensione(102400L);
        doc.setStato(StatoDocumento.IN_REVISIONE);
        doc.setVersione(1);
        doc.setCaricatoDa(cliente);

        DocumentoResponse dto = DocumentoMapper.toResponse(doc);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getNome()).isEqualTo("CUD_2024.pdf");
        assertThat(dto.getTipoFile()).isEqualTo("CUD");
        assertThat(dto.getDimensione()).isEqualTo(102400L);
        assertThat(dto.getStato()).isEqualTo(StatoDocumento.IN_REVISIONE);
        assertThat(dto.getVersione()).isEqualTo(1);
        assertThat(dto.getNomeCliente()).isEqualTo("Mario Rossi");
    }

    @Test
    @DisplayName("DocumentoMapper.toResponse: revisore null → nomeRevisore null")
    void documentoMapper_toResponse_senzaRevisore() {
        Documento doc = documentoBase();
        doc.setRevisore(null);

        DocumentoResponse dto = DocumentoMapper.toResponse(doc);

        assertThat(dto.getNomeRevisore()).isNull();
    }

    @Test
    @DisplayName("DocumentoMapper.toResponse: revisore presente → nomeRevisore settato")
    void documentoMapper_toResponse_conRevisore() {
        Collaboratore rev = new Collaboratore();
        rev.setNome("Luca");
        rev.setCognome("Bianchi");

        Documento doc = documentoBase();
        doc.setRevisore(rev);

        DocumentoResponse dto = DocumentoMapper.toResponse(doc);

        assertThat(dto.getNomeRevisore()).isEqualTo("Luca Bianchi");
    }

    @Test
    @DisplayName("DocumentoMapper.toModel: mappa nome, tipoFile, percorso e cliente")
    void documentoMapper_toModel() {
        DocumentoRequest req = new DocumentoRequest();
        req.setNome("Fattura.pdf");
        req.setTipoFile("FATTURA");
        req.setPercorsoFile("uploads/uuid_Fattura.pdf");
        req.setDimensione(50000L);

        Pratica pratica = new Pratica();
        pratica.setId(5L);
        Cliente cliente = clienteCon("Anna", "Verdi");

        Documento doc = DocumentoMapper.toModel(req, pratica, cliente);

        assertThat(doc.getNome()).isEqualTo("Fattura.pdf");
        assertThat(doc.getTipoFile()).isEqualTo("FATTURA");
        assertThat(doc.getPercorsoFile()).isEqualTo("uploads/uuid_Fattura.pdf");
        assertThat(doc.getPratica()).isEqualTo(pratica);
        assertThat(doc.getCaricatoDa()).isEqualTo(cliente);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UtenteMapper
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("UtenteMapper.toResponse: mappa tutti i campi correttamente")
    void utenteMapper_toResponse() {
        // Cliente.getRuolo() restituisce sempre Ruolo.CLIENTE (hardcoded nella sottoclasse)
        Cliente utente = new Cliente();
        utente.setId(99L);
        utente.setNome("Sara");
        utente.setCognome("Neri");
        utente.setEmail("sara@studio.it");
        utente.setEnabled(true);

        UtenteResponse dto = UtenteMapper.toResponse(utente);

        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getNome()).isEqualTo("Sara");
        assertThat(dto.getCognome()).isEqualTo("Neri");
        assertThat(dto.getEmail()).isEqualTo("sara@studio.it");
        assertThat(dto.getRuolo()).isEqualTo(Ruolo.CLIENTE);
        assertThat(dto.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("UtenteMapper.toResponse: utente disabilitato → enabled false")
    void utenteMapper_toResponse_disabilitato() {
        Cliente utente = new Cliente();
        utente.setId(1L);
        utente.setNome("X");
        utente.setCognome("Y");
        utente.setEmail("x@y.it");
        utente.setEnabled(false);

        assertThat(UtenteMapper.toResponse(utente).isEnabled()).isFalse();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NotificaMapper
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("NotificaMapper.toResponse: mappa tutti i campi correttamente")
    void notificaMapper_toResponse() {
        LocalDateTime ora = LocalDateTime.now();
        Notifica n = new Notifica();
        n.setId(5L);
        n.setMessaggio("Pratica aggiornata");
        n.setTipo(TipoNotifica.CAMBIO_STATO);
        n.setLetta(false);
        n.setDataCreazione(ora);

        NotificaResponse dto = NotificaMapper.toResponse(n);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getMessaggio()).isEqualTo("Pratica aggiornata");
        assertThat(dto.getTipo()).isEqualTo(TipoNotifica.CAMBIO_STATO);
        assertThat(dto.isLetta()).isFalse();
        assertThat(dto.getDataCreazione()).isEqualTo(ora);
    }

    @Test
    @DisplayName("NotificaMapper.toResponse: notifica letta → letta=true nel DTO")
    void notificaMapper_toResponse_letta() {
        Notifica n = new Notifica();
        n.setId(6L);
        n.setLetta(true);
        n.setMessaggio("test");

        assertThat(NotificaMapper.toResponse(n).isLetta()).isTrue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MessaggioChatMapper
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("MessaggioChatMapper.toResponse: mappa mittente, destinatario, testo e flag letto")
    void messaggioMapper_toResponse() {
        Cliente mittente = new Cliente();
        mittente.setId(1L);
        mittente.setNome("Mario");
        mittente.setCognome("Rossi");

        Commercialista dest = new Commercialista();
        dest.setId(2L);
        dest.setNome("Giulia");
        dest.setCognome("Bianchi");

        MessaggioChat m = new MessaggioChat();
        m.setId(100L);
        m.setMittente(mittente);
        m.setDestinatario(dest);
        m.setTesto("Ciao, ho una domanda");
        m.setLetto(false);

        MessaggioChatResponse dto = MessaggioChatMapper.toResponse(m);

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getMittenteId()).isEqualTo(1L);
        assertThat(dto.getMittenteNome()).isEqualTo("Mario Rossi");
        assertThat(dto.getDestinatarioId()).isEqualTo(2L);
        assertThat(dto.getDestinatarioNome()).isEqualTo("Giulia Bianchi");
        assertThat(dto.getTesto()).isEqualTo("Ciao, ho una domanda");
        assertThat(dto.isLetto()).isFalse();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Cliente clienteCon(String nome, String cognome) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setCognome(cognome);
        return c;
    }

    private Pratica praticaCon(Cliente cliente, Collaboratore collaboratore) {
        Pratica p = new Pratica();
        p.setId(1L);
        p.setTipoPratica(TipoPratica.DICHIARAZIONE_REDDITI);
        p.setStato(StatoPratica.BOZZA);
        p.setCliente(cliente);
        p.setAssegnataA(collaboratore);
        return p;
    }

    private Documento documentoBase() {
        Cliente c = clienteCon("Mario", "Rossi");
        Documento d = new Documento();
        d.setId(1L);
        d.setNome("test.pdf");
        d.setTipoFile("PDF");
        d.setDimensione(1000L);
        d.setStato(StatoDocumento.IN_REVISIONE);
        d.setVersione(1);
        d.setCaricatoDa(c);
        return d;
    }
}
