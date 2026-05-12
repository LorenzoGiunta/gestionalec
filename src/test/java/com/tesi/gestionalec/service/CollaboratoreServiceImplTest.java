package com.tesi.gestionalec.service;

import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.DocumentoRepo;
import com.tesi.gestionalec.repository.UtenteRepo;
import com.tesi.gestionalec.service.impl.CollaboratoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per CollaboratoreServiceImpl.
 * Copre: approvazione e rifiuto documenti (con motivazione).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CollaboratoreService – Unit Tests")
class CollaboratoreServiceImplTest {

    @Mock UtenteRepo utenteRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock CollaboratoreRepo collaboratoreRepo;
    @Mock DocumentoRepo documentoRepo;

    @InjectMocks
    CollaboratoreServiceImpl collaboratoreService;

    private Documento documento;

    @BeforeEach
    void setUp() {
        documento = new Documento();
        documento.setId(30L);
        documento.setNome("Fattura_2024.pdf");
        documento.setStato(StatoDocumento.IN_REVISIONE);
    }

    // ─── approvaDocumento ─────────────────────────────────────────────────────

    @Test
    @DisplayName("approvaDocumento imposta stato APPROVATO e salva")
    void approvaDocumento_impostaStatoApprovatoESalva() {
        when(documentoRepo.findById(30L)).thenReturn(Optional.of(documento));
        when(documentoRepo.save(any())).thenReturn(documento);

        collaboratoreService.approvaDocumento(30L);

        assertThat(documento.getStato()).isEqualTo(StatoDocumento.APPROVATO);
        verify(documentoRepo).save(documento);
    }

    @Test
    @DisplayName("approvaDocumento lancia eccezione se documento non trovato")
    void approvaDocumento_nonTrovato_lanciaEccezione() {
        when(documentoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collaboratoreService.approvaDocumento(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ─── rifiutaDocumento ─────────────────────────────────────────────────────

    @Test
    @DisplayName("rifiutaDocumento imposta stato RIFIUTATO con motivazione")
    void rifiutaDocumento_impostaStatoERifiuto() {
        when(documentoRepo.findById(30L)).thenReturn(Optional.of(documento));
        when(documentoRepo.save(any())).thenReturn(documento);

        collaboratoreService.rifiutaDocumento(30L, "Documento illeggibile");

        assertThat(documento.getStato()).isEqualTo(StatoDocumento.RIFIUTATO);
        assertThat(documento.getMotivazioneRifiuto()).isEqualTo("Documento illeggibile");
        verify(documentoRepo).save(documento);
    }

    @Test
    @DisplayName("rifiutaDocumento lancia eccezione se documento non trovato")
    void rifiutaDocumento_nonTrovato_lanciaEccezione() {
        when(documentoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> collaboratoreService.rifiutaDocumento(99L, "motivo"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("rifiutaDocumento con motivazione vuota salva comunque lo stato RIFIUTATO")
    void rifiutaDocumento_motivazioneVuota_salvaComunque() {
        when(documentoRepo.findById(30L)).thenReturn(Optional.of(documento));
        when(documentoRepo.save(any())).thenReturn(documento);

        collaboratoreService.rifiutaDocumento(30L, "");

        assertThat(documento.getStato()).isEqualTo(StatoDocumento.RIFIUTATO);
        assertThat(documento.getMotivazioneRifiuto()).isEmpty();
    }
}
