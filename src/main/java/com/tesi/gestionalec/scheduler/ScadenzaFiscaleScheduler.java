package com.tesi.gestionalec.scheduler;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.model.Pratica;
import com.tesi.gestionalec.model.TipoNotifica;
import com.tesi.gestionalec.observer.GestoreNotifiche;
import com.tesi.gestionalec.service.interfaces.PraticaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScadenzaFiscaleScheduler {

    private final PraticaService praticaService;
    private final GestoreNotifiche gestoreNotifiche;

    // il lucchetto — uno solo per tutti i thread
    private final Lock lock = new ReentrantLock();

    // viene eseguito ogni giorno a mezzanotte
    @Scheduled(cron = "0 0 0 * * *")
    public void controllaScadenze() {
        log.info("Controllo scadenze fiscali avviato...");

        lock.lock();   // prendo il lucchetto 🔒
        try {
            List<Pratica> pratiche = praticaService.trovaTutte()
                    .stream()
                    .filter(p -> p.getScadenza() != null)
                    .toList();

            for (Pratica pratica : pratiche) {
                LocalDate scadenza = pratica.getScadenza();
                LocalDate oggi = LocalDate.now();
                long giorniMancanti = oggi.until(scadenza).getDays();

                // notifica se mancano 7 giorni o meno
                if (giorniMancanti >= 0 && giorniMancanti <= 7) {
                    Notifica notifica = new Notifica();
                    notifica.setDestinatario(pratica.getCliente());
                    notifica.setMessaggio("La pratica tipo : \"" + pratica.getTipoPratica()
                            + "\" scade tra " + giorniMancanti + " giorni!");
                    notifica.setTipo(TipoNotifica.CAMBIO_STATO);
                    notifica.setLetta(false);

                    gestoreNotifiche.notificaTutti(notifica);
                    log.info("Notifica scadenza inviata per pratica: {}", pratica.getId());
                }
            }
        } finally {
            lock.unlock();   // rilascio sempre il lucchetto 🔓 — anche in caso di errore
        }

        log.info("Controllo scadenze fiscali completato.");
    }
}