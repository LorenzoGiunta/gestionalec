package com.tesi.gestionalec.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void inviaEmail(String destinatario, String oggetto, String testo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(oggetto);
            helper.setText(testo, true); // true = HTML abilitato

            mailSender.send(message);
        } catch (MessagingException e) {
            // logga l'errore senza bloccare l'applicazione
            System.err.println("Errore invio email a " + destinatario + ": " + e.getMessage());
        }
    }
}