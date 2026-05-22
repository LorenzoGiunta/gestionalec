package com.tesi.gestionalec.state;

import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.StatoPratica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Test unitari per il Pattern State delle Pratiche.
 * Nessuna dipendenza Spring/DB: testano la logica pura di avanzamento stato.
 *
 * Flusso atteso:
 *   BOZZA → IN_LAVORAZIONE → IN_ATTESA_DOCUMENTI → COMPLETATA (terminale)
 */
@DisplayName("State Pattern – Pratica")
class StatoPraticaStateTest {

    private Pratica pratica;

    @BeforeEach
    void setUp() {
        pratica = new Pratica();
    }

    // ─── BOZZA ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("BozzaState.avanza() → IN_LAVORAZIONE")
    void bozza_avanza_aInLavorazione() {
        pratica.setStato(StatoPratica.BOZZA);
        pratica.setStatoCorrente(new BozzaState());

        pratica.getStatoCorrente().avanza(pratica);

        assertThat(pratica.getStato()).isEqualTo(StatoPratica.IN_LAVORAZIONE);
        assertThat(pratica.getStatoCorrente()).isInstanceOf(InLavorazioneState.class);
    }

    @Test
    @DisplayName("BozzaState.getStato() restituisce BOZZA")
    void bozza_getStato() {
        assertThat(new BozzaState().getStato()).isEqualTo(StatoPratica.BOZZA);
    }

    // ─── IN_LAVORAZIONE ───────────────────────────────────────────────────────

    @Test
    @DisplayName("InLavorazioneState.avanza() → IN_ATTESA_DOCUMENTI")
    void inLavorazione_avanza_aInAttesaDocumenti() {
        pratica.setStato(StatoPratica.IN_LAVORAZIONE);
        pratica.setStatoCorrente(new InLavorazioneState());

        pratica.getStatoCorrente().avanza(pratica);

        assertThat(pratica.getStato()).isEqualTo(StatoPratica.IN_ATTESA_DOCUMENTI);
        assertThat(pratica.getStatoCorrente()).isInstanceOf(InAttesaDocumentiState.class);
    }

    @Test
    @DisplayName("InLavorazioneState.getStato() restituisce IN_LAVORAZIONE")
    void inLavorazione_getStato() {
        assertThat(new InLavorazioneState().getStato()).isEqualTo(StatoPratica.IN_LAVORAZIONE);
    }

    // ─── IN_ATTESA_DOCUMENTI ──────────────────────────────────────────────────

    @Test
    @DisplayName("InAttesaDocumentiState.avanza() → COMPLETATA")
    void inAttesaDocumenti_avanza_aCompletata() {
        pratica.setStato(StatoPratica.IN_ATTESA_DOCUMENTI);
        pratica.setStatoCorrente(new InAttesaDocumentiState());

        pratica.getStatoCorrente().avanza(pratica);

        assertThat(pratica.getStato()).isEqualTo(StatoPratica.COMPLETATA);
        assertThat(pratica.getStatoCorrente()).isInstanceOf(CompletataState.class);
    }

    @Test
    @DisplayName("InAttesaDocumentiState.getStato() restituisce IN_ATTESA_DOCUMENTI")
    void inAttesaDocumenti_getStato() {
        assertThat(new InAttesaDocumentiState().getStato()).isEqualTo(StatoPratica.IN_ATTESA_DOCUMENTI);
    }

    // ─── COMPLETATA (terminale) ────────────────────────────────────────────────

    @Test
    @DisplayName("CompletataState.avanza() lancia IllegalStateException")
    void completata_avanza_lanciaEccezione() {
        pratica.setStato(StatoPratica.COMPLETATA);
        pratica.setStatoCorrente(new CompletataState());

        assertThatThrownBy(() -> pratica.getStatoCorrente().avanza(pratica))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("completat"); // "completatta" nel sorgente
    }

    @Test
    @DisplayName("CompletataState.getStato() restituisce COMPLETATA")
    void completata_getStato() {
        assertThat(new CompletataState().getStato()).isEqualTo(StatoPratica.COMPLETATA);
    }

    // ─── Flusso completo end-to-end ────────────────────────────────────────────

    @Test
    @DisplayName("Flusso completo: BOZZA → IN_LAVORAZIONE → IN_ATTESA_DOCUMENTI → COMPLETATA")
    void flussoDiAvanzamentoCompleto() {
        pratica.setStato(StatoPratica.BOZZA);
        pratica.setStatoCorrente(new BozzaState());

        pratica.getStatoCorrente().avanza(pratica);
        assertThat(pratica.getStato()).isEqualTo(StatoPratica.IN_LAVORAZIONE);

        pratica.getStatoCorrente().avanza(pratica);
        assertThat(pratica.getStato()).isEqualTo(StatoPratica.IN_ATTESA_DOCUMENTI);

        pratica.getStatoCorrente().avanza(pratica);
        assertThat(pratica.getStato()).isEqualTo(StatoPratica.COMPLETATA);

        // Lo stato terminale non si può più avanzare
        assertThatThrownBy(() -> pratica.getStatoCorrente().avanza(pratica))
                .isInstanceOf(IllegalStateException.class);
    }
}
