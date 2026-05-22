package com.tesi.gestionalec.service;

import com.tesi.gestionalec.dto.request.MessaggioChatRequest;
import com.tesi.gestionalec.dto.response.MessaggioChatResponse;
import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.repository.MessaggioChatRepo;
import com.tesi.gestionalec.service.impl.ChatServiceImpl;
import com.tesi.gestionalec.service.interfaces.UtenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per ChatServiceImpl.
 * Copre: invio messaggi tra ruoli validi, blocco dell'Amministratore,
 * blocco di combinazioni di ruoli non permesse, e recupero storico.
 *
 * Nota: getRuolo() è hardcoded nelle sottoclassi concrete di Utente
 * (es. new Cliente() → Ruolo.CLIENTE), quindi si usano direttamente quelle.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService – Unit Tests")
class ChatServiceImplTest {

    @Mock MessaggioChatRepo repo;
    @Mock UtenteService utenteService;

    @InjectMocks
    ChatServiceImpl chatService;

    private MessaggioChatRequest request;

    @BeforeEach
    void setUp() {
        request = new MessaggioChatRequest();
        request.setDestinatarioId(2L);
        request.setTesto("Ciao!");
    }

    // ─── Helper per costruire MessaggioChat salvato ───────────────────────────
    private void mockSave() {
        when(repo.save(any())).thenAnswer(inv -> {
            MessaggioChat m = inv.getArgument(0);
            m.setId(100L);
            return m;
        });
    }

    private Cliente cliente(long id) {
        Cliente c = new Cliente(); c.setId(id); c.setNome("M"); c.setCognome("R"); return c;
    }
    private Commercialista commercialista(long id) {
        Commercialista c = new Commercialista(); c.setId(id); c.setNome("G"); c.setCognome("B"); return c;
    }
    private Collaboratore collaboratore(long id) {
        Collaboratore c = new Collaboratore(); c.setId(id); c.setNome("L"); c.setCognome("V"); return c;
    }
    private Amministratore amministratore(long id) {
        Amministratore a = new Amministratore(); a.setId(id); a.setNome("A"); a.setCognome("A"); return a;
    }

    // ─── Combinazioni VALIDE ──────────────────────────────────────────────────

    @Test
    @DisplayName("Cliente → Commercialista: messaggio salvato con successo")
    void salvaEInvia_clienteACommercialista_ok() {
        when(utenteService.trovaPerId(2L)).thenReturn(commercialista(2L));
        mockSave();

        MessaggioChatResponse resp = chatService.salvaEInvia(request, cliente(1L));

        assertThat(resp).isNotNull();
        verify(repo).save(any(MessaggioChat.class));
    }

    @Test
    @DisplayName("Cliente → Collaboratore: messaggio salvato con successo")
    void salvaEInvia_clienteACollaboratore_ok() {
        when(utenteService.trovaPerId(2L)).thenReturn(collaboratore(2L));
        mockSave();

        assertThatCode(() -> chatService.salvaEInvia(request, cliente(1L))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Commercialista → Cliente: messaggio salvato con successo")
    void salvaEInvia_commercialistaACliente_ok() {
        when(utenteService.trovaPerId(2L)).thenReturn(cliente(2L));
        mockSave();

        assertThatCode(() -> chatService.salvaEInvia(request, commercialista(1L))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Collaboratore → Commercialista: messaggio salvato con successo")
    void salvaEInvia_collaboratoreACommercialista_ok() {
        when(utenteService.trovaPerId(2L)).thenReturn(commercialista(2L));
        mockSave();

        assertThatCode(() -> chatService.salvaEInvia(request, collaboratore(1L))).doesNotThrowAnyException();
    }

    // ─── Combinazioni NON VALIDE ──────────────────────────────────────────────

    @Test
    @DisplayName("Amministratore come mittente → AccessDeniedException, repo non chiamato")
    void salvaEInvia_amministratoreComeMittente_lanciaAccessDenied() {
        when(utenteService.trovaPerId(2L)).thenReturn(cliente(2L));

        assertThatThrownBy(() -> chatService.salvaEInvia(request, amministratore(1L)))
                .isInstanceOf(AccessDeniedException.class);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Cliente → Cliente: combinazione non valida → AccessDeniedException")
    void salvaEInvia_clienteACliente_lanciaAccessDenied() {
        when(utenteService.trovaPerId(2L)).thenReturn(cliente(2L));

        assertThatThrownBy(() -> chatService.salvaEInvia(request, cliente(1L)))
                .isInstanceOf(AccessDeniedException.class);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Cliente → Amministratore: combinazione non valida → AccessDeniedException")
    void salvaEInvia_clienteAAmministratore_lanciaAccessDenied() {
        when(utenteService.trovaPerId(2L)).thenReturn(amministratore(2L));

        assertThatThrownBy(() -> chatService.salvaEInvia(request, cliente(1L)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Commercialista → Commercialista: combinazione non valida → AccessDeniedException")
    void salvaEInvia_commercialistaACommercialista_lanciaAccessDenied() {
        when(utenteService.trovaPerId(2L)).thenReturn(commercialista(2L));

        assertThatThrownBy(() -> chatService.salvaEInvia(request, commercialista(1L)))
                .isInstanceOf(AccessDeniedException.class);
        verify(repo, never()).save(any());
    }

    // ─── storico ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("storico: delega al repo e mappa correttamente i messaggi in DTO")
    void storico_delegaAlRepoEMappa() {
        Cliente mitt = cliente(1L);
        Commercialista dest = commercialista(2L);

        MessaggioChat msg = new MessaggioChat();
        msg.setId(10L);
        msg.setMittente(mitt);
        msg.setDestinatario(dest);
        msg.setTesto("Ciao");
        msg.setLetto(true);

        when(repo.trovaStotico(1L, 2L)).thenReturn(List.of(msg));

        List<MessaggioChatResponse> storico = chatService.storico(1L, 2L);

        assertThat(storico).hasSize(1);
        assertThat(storico.get(0).getTesto()).isEqualTo("Ciao");
        verify(repo).trovaStotico(1L, 2L);
    }

    @Test
    @DisplayName("storico: nessun messaggio → lista vuota")
    void storico_vuoto_listaVuota() {
        when(repo.trovaStotico(1L, 2L)).thenReturn(List.of());

        assertThat(chatService.storico(1L, 2L)).isEmpty();
    }
}
