package com.tesi.gestionalec.service;

import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.DocumentoRepo;
import com.tesi.gestionalec.service.impl.DocumentoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per DocumentoServiceImpl.
 * Copre: caricamento iniziale, versionamento, approvazione/rifiuto, assegnazione revisore.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentoService – Unit Tests")
class DocumentoServiceImplTest {

    @Mock DocumentoRepo documentoRepo;
    @Mock CollaboratoreRepo collaboratoreRepo;

    @InjectMocks
    DocumentoServiceImpl documentoService;

    private Documento documentoBase;
    private Collaboratore revisore;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Anna");
        cliente.setCognome("Verdi");

        Pratica pratica = new Pratica();
        pratica.setId(10L);

        documentoBase = new Documento();
        documentoBase.setId(20L);
        documentoBase.setNome("CUD_2024.pdf");
        documentoBase.setTipoFile("CUD");
        documentoBase.setPercorsoFile("uploads/documenti/uuid_CUD_2024.pdf");
        documentoBase.setDimensione(102400L);
        documentoBase.setPratica(pratica);
        documentoBase.setCaricatoDa(cliente);
        documentoBase.setVersione(1);
        documentoBase.setStato(StatoDocumento.IN_REVISIONE);

        revisore = new Collaboratore();
        revisore.setId(5L);
        revisore.setNome("Luca");
        revisore.setCognome("Bianchi");
    }

    // ─── caricaDocumento ──────────────────────────────────────────────────────

    @Test
    @DisplayName("caricaDocumento imposta versione 1 e stato IN_REVISIONE")
    void caricaDocumento_impostaVersioneUnoEInRevisione() {
        Documento nuovo = new Documento();
        nuovo.setNome("Fattura.pdf");
        when(documentoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Documento salvato = documentoService.caricaDocumento(nuovo);

        assertThat(salvato.getVersione()).isEqualTo(1);
        assertThat(salvato.getStato()).isEqualTo(StatoDocumento.IN_REVISIONE);
        verify(documentoRepo).save(nuovo);
    }

    // ─── trovaPerId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("trovaPerId restituisce il documento se esiste")
    void trovaPerId_esistente() {
        when(documentoRepo.findById(20L)).thenReturn(Optional.of(documentoBase));

        Documento trovato = documentoService.trovaPerId(20L);

        assertThat(trovato.getId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("trovaPerId lancia RuntimeException se non esiste")
    void trovaPerId_nonEsistente_lanciaEccezione() {
        when(documentoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentoService.trovaPerId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ─── nuovaVersione ────────────────────────────────────────────────────────

    @Test
    @DisplayName("nuovaVersione incrementa il numero di versione del documento")
    void nuovaVersione_incrementaVersione() {
        documentoBase.setVersione(2);
        when(documentoRepo.findById(20L)).thenReturn(Optional.of(documentoBase));
        when(documentoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Documento nuovoDoc = new Documento();
        nuovoDoc.setNome("CUD_2024_v2.pdf");

        Documento salvato = documentoService.nuovaVersione(20L, nuovoDoc);

        assertThat(salvato.getVersione()).isEqualTo(3); // 2 + 1
        assertThat(salvato.getStato()).isEqualTo(StatoDocumento.IN_REVISIONE);
    }

    @Test
    @DisplayName("nuovaVersione eredita pratica e cliente dal documento originale")
    void nuovaVersione_ereditaPraticaECliente() {
        when(documentoRepo.findById(20L)).thenReturn(Optional.of(documentoBase));
        when(documentoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Documento nuovoDoc = new Documento();
        Documento salvato = documentoService.nuovaVersione(20L, nuovoDoc);

        assertThat(salvato.getPratica()).isEqualTo(documentoBase.getPratica());
        assertThat(salvato.getCaricatoDa()).isEqualTo(documentoBase.getCaricatoDa());
    }

    // ─── assegnaRevisore ──────────────────────────────────────────────────────

    @Test
    @DisplayName("assegnaRevisore setta il collaboratore sul documento")
    void assegnaRevisore_settaIlCollaboratore() {
        when(documentoRepo.findById(20L)).thenReturn(Optional.of(documentoBase));
        when(collaboratoreRepo.findById(5L)).thenReturn(Optional.of(revisore));
        when(documentoRepo.save(any())).thenReturn(documentoBase);

        documentoService.assegnaRevisore(20L, 5L);

        assertThat(documentoBase.getRevisore()).isEqualTo(revisore);
        verify(documentoRepo).save(documentoBase);
    }

    @Test
    @DisplayName("assegnaRevisore lancia eccezione se collaboratore non trovato")
    void assegnaRevisore_collaboratoreNonTrovato_lanciaEccezione() {
        when(documentoRepo.findById(20L)).thenReturn(Optional.of(documentoBase));
        when(collaboratoreRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentoService.assegnaRevisore(20L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}
