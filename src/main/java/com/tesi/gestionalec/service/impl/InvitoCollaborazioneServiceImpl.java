package com.tesi.gestionalec.service.impl;

import com.tesi.gestionalec.exception.DuplicateInviteException;
import com.tesi.gestionalec.exception.ForbiddenOperationException;
import com.tesi.gestionalec.exception.InvalidStateException;
import com.tesi.gestionalec.exception.ResourceNotFoundException;
import com.tesi.gestionalec.model.*;
import com.tesi.gestionalec.repository.CollaboratoreRepo;
import com.tesi.gestionalec.repository.CommercialistaRepo;
import com.tesi.gestionalec.repository.InvitoCollaborazioneRepo;
import com.tesi.gestionalec.repository.NotificaRepo;
import com.tesi.gestionalec.service.interfaces.InvitoCollaborazioneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvitoCollaborazioneServiceImpl implements InvitoCollaborazioneService {

    private final InvitoCollaborazioneRepo invitoRepo;
    private final CommercialistaRepo commercialistaRepo;
    private final CollaboratoreRepo collaboratoreRepo;
    private final NotificaRepo notificaRepo;
    private final EmailService emailService;

    /** URL base del frontend, iniettato da application.properties: app.frontend-url */
    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // ─────────────────────────────────────────────────────────────────────────
    // INVIO INVITO
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public InvitoCollaborazione invita(Long commercialistaId, String emailDestinatario) {
        Commercialista comm = commercialistaRepo.findById(commercialistaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commercialista", "id", commercialistaId));

        // Impedisce duplicati: un solo invito PENDING per coppia commercialista+email
        if (invitoRepo.existsByCommercialista_IdAndEmailDestinatarioAndStato(
                commercialistaId, emailDestinatario, StatoInvito.PENDING)) {
            throw new DuplicateInviteException(emailDestinatario);
        }

        // Costruisce l'entità
        InvitoCollaborazione invito = new InvitoCollaborazione();
        invito.setCommercialista(comm);
        invito.setEmailDestinatario(emailDestinatario);
        invito.setToken(UUID.randomUUID().toString());
        invito.setStato(StatoInvito.PENDING);
        invito.setScadeIl(LocalDateTime.now().plusDays(7));
        // creatoIl è gestito da @CreationTimestamp — non serve impostarlo manualmente

        // Se il destinatario è già registrato come Collaboratore, collegalo subito
        collaboratoreRepo.findByEmail(emailDestinatario)
                .ifPresent(collab -> {
                    invito.setCollaboratore(collab);
                    log.info("Destinatario {} già registrato — collegato subito all'invito", emailDestinatario);
                });

        InvitoCollaborazione salvato = invitoRepo.save(invito);

        // Invia email asincrona (usa il pool emailExecutor già configurato)
        String nomeCommercialista = comm.getNome() + " " + comm.getCognome();
        inviaEmailInvito(emailDestinatario, nomeCommercialista, salvato.getToken());

        log.info("Invito creato [id={}] da commercialista {} verso {}",
                salvato.getId(), commercialistaId, emailDestinatario);

        return salvato;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACCETTAZIONE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void accetta(String token, Long collaboratoreId) {
        InvitoCollaborazione invito = findInvitoValidoByToken(token);

        Collaboratore collab = collaboratoreRepo.findById(collaboratoreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collaboratore", "id", collaboratoreId));

        // Verifica che l'email del collaboratore corrisponda al destinatario dell'invito
        if (!collab.getEmail().equalsIgnoreCase(invito.getEmailDestinatario())) {
            throw new ForbiddenOperationException(
                    "La tua email non corrisponde al destinatario dell'invito");
        }

        invito.setCollaboratore(collab);
        invito.setStato(StatoInvito.ACCEPTED);

        // Notifica in-app al Commercialista
        creaNotifica(
                invito.getCommercialista(),
                collab.getNome() + " " + collab.getCognome() + " ha accettato il tuo invito di collaborazione.",
                TipoNotifica.CAMBIO_STATO
        );

        log.info("Invito [id={}] accettato dal collaboratore {}", invito.getId(), collaboratoreId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RIFIUTO
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void rifiuta(String token) {
        InvitoCollaborazione invito = findInvitoValidoByToken(token);
        invito.setStato(StatoInvito.DECLINED);

        // Notifica opzionale al commercialista
        String msg = "L'invito inviato a " + invito.getEmailDestinatario() + " è stato rifiutato.";
        creaNotifica(invito.getCommercialista(), msg, TipoNotifica.CAMBIO_STATO);

        log.info("Invito [id={}] rifiutato (email destinatario: {})",
                invito.getId(), invito.getEmailDestinatario());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REVOCA
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void revoca(Long invitoId, Long commercialistaId) {
        InvitoCollaborazione invito = invitoRepo.findById(invitoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invito", "id", invitoId));

        // Verifica che il richiedente sia il commercialista proprietario dell'invito
        if (!invito.getCommercialista().getId().equals(commercialistaId)) {
            throw new ForbiddenOperationException(
                    "Non sei autorizzato a revocare questo invito");
        }

        // Non si può revocare un invito già rifiutato o scaduto
        if (invito.getStato() == StatoInvito.DECLINED || invito.getStato() == StatoInvito.EXPIRED) {
            throw new InvalidStateException(
                    "Invito", invito.getStato().name(), "revoca");
        }

        invito.setStato(StatoInvito.DECLINED);
        invito.setCollaboratore(null); // dissocia il collaboratore

        log.info("Invito [id={}] revocato dal commercialista {}", invitoId, commercialistaId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // QUERY
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<InvitoCollaborazione> trovaPerCommercialista(Long commercialistaId) {
        return invitoRepo.findByCommercialista_Id(commercialistaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitoCollaborazione> trovaPendingPerEmail(String email) {
        return invitoRepo.findByEmailDestinatarioAndStato(email, StatoInvito.PENDING);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SCHEDULER — Scadenza automatica
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Ogni notte alle 01:00 marca come EXPIRED tutti gli inviti PENDING scaduti.
     * Il lock su this è sufficiente per un singolo nodo; in cluster usare ShedLock.
     */
    @Override
    @Scheduled(cron = "0 0 1 * * *")
    public void scadenzaAutomatica() {
        List<InvitoCollaborazione> scaduti = invitoRepo
                .findByStatoAndScadeIlBefore(StatoInvito.PENDING, LocalDateTime.now());

        if (!scaduti.isEmpty()) {
            scaduti.forEach(i -> i.setStato(StatoInvito.EXPIRED));
            invitoRepo.saveAll(scaduti);
            log.info("Scadenza automatica: {} inviti marcati come EXPIRED", scaduti.size());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Recupera un invito tramite token e valida che sia ancora PENDING e non scaduto.
     * Lancia eccezioni descrittive per ogni caso di errore.
     */
    private InvitoCollaborazione findInvitoValidoByToken(String token) {
        InvitoCollaborazione invito = invitoRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invito", "token", token));

        if (invito.getStato() != StatoInvito.PENDING) {
            throw new InvalidStateException(
                    "Invito", invito.getStato().name(), "accetta/rifiuta");
        }

        if (invito.getScadeIl().isBefore(LocalDateTime.now())) {
            invito.setStato(StatoInvito.EXPIRED);
            throw new InvalidStateException(
                    "L'invito è scaduto il " + invito.getScadeIl());
        }

        return invito;
    }

    /** Crea e persiste una Notifica in-app per un Utente. */
    private void creaNotifica(Utente destinatario, String messaggio, TipoNotifica tipo) {
        Notifica notifica = new Notifica();
        notifica.setDestinatario(destinatario);
        notifica.setMessaggio(messaggio);
        notifica.setTipo(tipo);
        notifica.setLetta(false);
        notificaRepo.save(notifica);
    }

    /** Costruisce e invia asincronamente l'email di invito con link HTML. */
    private void inviaEmailInvito(String emailDestinatario, String nomeCommercialista, String token) {
        String linkAccetta = frontendUrl + "/invito/" + token + "/accetta";
        String linkRifiuta = frontendUrl + "/invito/" + token + "/rifiuta";

        String corpo = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <div style="background: #1a2744; padding: 24px; border-radius: 8px 8px 0 0;">
                    <h1 style="color: #f59e0b; margin: 0; font-size: 22px;">StudioFiscale</h1>
                  </div>
                  <div style="padding: 32px; background: #ffffff; border: 1px solid #e5e7eb;">
                    <h2 style="color: #1a2744; margin-top: 0;">Hai ricevuto un invito di collaborazione</h2>
                    <p style="color: #4b5563; line-height: 1.6;">
                      Il commercialista <strong>%s</strong> ti ha invitato a collaborare
                      tramite la piattaforma <strong>StudioFiscale</strong>.
                    </p>
                    <p style="color: #4b5563;">L'invito scade tra <strong>7 giorni</strong>.</p>
                    <div style="margin: 32px 0; display: flex; gap: 12px;">
                      <a href="%s"
                         style="background: #1a2744; color: white; padding: 12px 24px;
                                text-decoration: none; border-radius: 6px; font-weight: bold;">
                        ✓ Accetta Invito
                      </a>
                      &nbsp;&nbsp;
                      <a href="%s"
                         style="background: #f3f4f6; color: #374151; padding: 12px 24px;
                                text-decoration: none; border-radius: 6px;">
                        ✗ Rifiuta
                      </a>
                    </div>
                    <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 24px 0;">
                    <p style="color: #9ca3af; font-size: 12px;">
                      Se non ti aspettavi questo invito, puoi ignorare questa email.
                    </p>
                  </div>
                </div>
                """.formatted(nomeCommercialista, linkAccetta, linkRifiuta);

        emailService.inviaEmail(
                emailDestinatario,
                "Invito di collaborazione — StudioFiscale",
                corpo
        );
    }
}
