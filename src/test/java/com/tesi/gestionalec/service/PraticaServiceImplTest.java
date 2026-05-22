package com.tesi.gestionalec.service;

import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.observer.GestoreNotifiche;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.PraticaRepo;
import com.tesi.gestionalec.service.impl.PraticaServiceImpl;
import com.tesi.gestionalec.state.BozzaState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per PraticaServiceImpl.
 * Usa Mockito per isolare il service da repository e observer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PraticaService – Unit Tests")
class PraticaServiceImplTest {

    @Mock PraticaRepo praticaRepo;
    @Mock CollaboratoreRepo collaboratoreRepo;
    @Mock GestoreNotifiche gestoreNotifiche;

    @InjectMocks
    PraticaServiceImpl praticaService;

    private Cliente cliente;
    private Pratica praticaBase;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Mario");
        cliente.setCognome("Rossi");

        praticaBase = new Pratica();
        praticaBase.setId(10L);
        praticaBase.setCliente(cliente);
        praticaBase.setTipoPratica(TipoPratica.DICHIARAZIONE_REDDITI);
        praticaBase.setStato(StatoPratica.BOZZA);
        praticaBase.setStatoCorrente(new BozzaState());
    }

    // ─── creaPratica ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("creaPratica imposta stato BOZZA e salva nel repo")
    void creaPratica_impostaStatoBozzaESalva() {
        when(praticaRepo.save(any(Pratica.class))).thenReturn(praticaBase);

        Pratica nuova = new Pratica();
        nuova.setCliente(cliente);
        nuova.setTipoPratica(TipoPratica.DICHIARAZIONE_REDDITI);

        Pratica salvata = praticaService.creaPratica(nuova);

        assertThat(salvata.getStato()).isEqualTo(StatoPratica.BOZZA);
        verify(praticaRepo, times(1)).save(nuova);
    }

    @Test
    @DisplayName("creaPratica notifica il cliente tramite GestoreNotifiche")
    void creaPratica_notificaIlCliente() {
        when(praticaRepo.save(any())).thenReturn(praticaBase);

        praticaService.creaPratica(praticaBase);

        ArgumentCaptor<Notifica> captor = ArgumentCaptor.forClass(Notifica.class);
        verify(gestoreNotifiche).notificaTutti(captor.capture());

        Notifica notifica = captor.getValue();
        assertThat(notifica.getDestinatario()).isEqualTo(cliente);
        assertThat(notifica.getTipo()).isEqualTo(TipoNotifica.CAMBIO_STATO);
        assertThat(notifica.isLetta()).isFalse();
    }

    // ─── trovaPerId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("trovaPerId restituisce la pratica se esiste")
    void trovaPerId_esistente_restituiscePratica() {
        when(praticaRepo.findById(10L)).thenReturn(Optional.of(praticaBase));

        Pratica trovata = praticaService.trovaPerId(10L);

        assertThat(trovata.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("trovaPerId lancia RuntimeException se non esiste")
    void trovaPerId_nonEsistente_lanciaEccezione() {
        when(praticaRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> praticaService.trovaPerId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ─── avanzaStato ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("avanzaStato: da BOZZA passa a IN_LAVORAZIONE e salva")
    void avanzaStato_daBozza_passaAInLavorazione() {
        when(praticaRepo.findById(10L)).thenReturn(Optional.of(praticaBase));
        when(praticaRepo.save(any())).thenReturn(praticaBase);

        praticaService.avanzaStato(10L);

        assertThat(praticaBase.getStato()).isEqualTo(StatoPratica.IN_LAVORAZIONE);
        verify(praticaRepo).save(praticaBase);
    }

    @Test
    @DisplayName("avanzaStato notifica il cliente del nuovo stato")
    void avanzaStato_notificaIlCliente() {
        when(praticaRepo.findById(10L)).thenReturn(Optional.of(praticaBase));
        when(praticaRepo.save(any())).thenReturn(praticaBase);

        praticaService.avanzaStato(10L);

        verify(gestoreNotifiche).notificaTutti(any(Notifica.class));
    }

    // ─── assegnaCollaboratore ─────────────────────────────────────────────────

    @Test
    @DisplayName("assegnaCollaboratore: setta il collaboratore sulla pratica e salva")
    void assegnaCollaboratore_setCollaboratoreESalva() {
        Collaboratore col = new Collaboratore();
        col.setId(5L);
        col.setNome("Luca");
        col.setCognome("Bianchi");

        when(praticaRepo.findById(10L)).thenReturn(Optional.of(praticaBase));
        when(collaboratoreRepo.findById(5L)).thenReturn(Optional.of(col));
        when(praticaRepo.save(any())).thenReturn(praticaBase);

        praticaService.assegnaCollaboratore(10L, 5L);

        assertThat(praticaBase.getAssegnataA()).isEqualTo(col);
        verify(praticaRepo).save(praticaBase);
    }

    @Test
    @DisplayName("assegnaCollaboratore notifica il collaboratore assegnato")
    void assegnaCollaboratore_notificaIlCollaboratore() {
        Collaboratore col = new Collaboratore();
        col.setId(5L);
        col.setNome("Luca");
        col.setCognome("Bianchi");

        when(praticaRepo.findById(10L)).thenReturn(Optional.of(praticaBase));
        when(collaboratoreRepo.findById(5L)).thenReturn(Optional.of(col));
        when(praticaRepo.save(any())).thenReturn(praticaBase);

        praticaService.assegnaCollaboratore(10L, 5L);

        ArgumentCaptor<Notifica> captor = ArgumentCaptor.forClass(Notifica.class);
        verify(gestoreNotifiche).notificaTutti(captor.capture());
        assertThat(captor.getValue().getDestinatario()).isEqualTo(col);
    }

    @Test
    @DisplayName("assegnaCollaboratore lancia eccezione se collaboratore non esiste")
    void assegnaCollaboratore_collaboratoreNonEsistente_lanciaEccezione() {
        when(praticaRepo.findById(10L)).thenReturn(Optional.of(praticaBase));
        when(collaboratoreRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> praticaService.assegnaCollaboratore(10L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ─── trovaTutte / trovaPerStato ───────────────────────────────────────────

    @Test
    @DisplayName("trovaTutte delega al repository findAll")
    void trovaTutte_delegaAlRepo() {
        when(praticaRepo.findAll()).thenReturn(List.of(praticaBase));

        List<Pratica> result = praticaService.trovaTutte();

        assertThat(result).hasSize(1);
        verify(praticaRepo).findAll();
    }

    @Test
    @DisplayName("trovaPerStato filtra per stato BOZZA")
    void trovaPerStato_filtroCorretto() {
        when(praticaRepo.findByStato(StatoPratica.BOZZA)).thenReturn(List.of(praticaBase));

        List<Pratica> result = praticaService.trovaPerStato(StatoPratica.BOZZA);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStato()).isEqualTo(StatoPratica.BOZZA);
    }
}
