package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.observer.interfaces.NotificaObserver;
import com.tesi.gestionalec.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificaObserver implements NotificaObserver {

    private final EmailService emailService;

    /**
     * Invio asincrono della notifica via email.
     * Gira su un thread del pool "notificaExecutor" → il thread HTTP viene
     * liberato immediatamente senza aspettare la risposta SMTP.
     * @Async funziona perché viene chiamato dall'esterno del bean (via proxy Spring AOP).
     */
    @Override
    @Async("notificaExecutor")
    public void aggiorna(Notifica notifica) {
        String email = notifica.getDestinatario().getEmail();

        String oggetto = switch (notifica.getTipo()) {
            case CAMBIO_STATO       -> "Aggiornamento pratica";
            case DOCUMENTO_CARICATO -> "Nuovo documento caricato";
            default                 -> "Notifica gestionale";
        };

        emailService.inviaEmail(email, oggetto, costruisciHtml(notifica));
    }

    private String costruisciHtml(Notifica notifica) {
        return """
                <html><body>
                <h2>Ciao %s,</h2>
                <p>%s</p>
                <br><small>Gestionale Commercialista</small>
                </body></html>
                """.formatted(
                notifica.getDestinatario().getNome(),
                notifica.getMessaggio()
        );
    }
}