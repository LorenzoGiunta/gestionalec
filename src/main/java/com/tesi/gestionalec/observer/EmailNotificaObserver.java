package com.tesi.gestionalec.observer;

import com.tesi.gestionalec.model.Notifica;
import com.tesi.gestionalec.observer.interfaces.NotificaObserver;
import com.tesi.gestionalec.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificaObserver implements NotificaObserver {

    private final EmailService emailService;

    @Override
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