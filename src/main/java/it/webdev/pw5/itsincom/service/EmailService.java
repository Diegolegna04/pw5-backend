
package it.webdev.pw5.itsincom.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendBookingConfirmation(String to, String subject, String body) {
        mailer.send(Mail.withText(to, subject, body));
    }

    public void sendVerificationEmail(String email, String message) {
        mailer.send(Mail.withHtml(email, "Verifica la tua email", message));
    }
}