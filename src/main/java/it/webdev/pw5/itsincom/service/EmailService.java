package it.webdev.pw5.itsincom.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

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

    public void sendBookingAccepted(String userEmail, String bookingConfirmation, String s, String ticketUrl) {
        mailer.send(Mail.withHtml(userEmail, "Booking accepted",
                bookingConfirmation +
                        "<br><a href=\"" +
                        ticketUrl +
                        "\">Download ticket</a>"));
    }

    public void sendUpdateEmailToParticipants(List<String> emails, String subject, String body) {
        for (String email : emails) {
            mailer.send(Mail.withHtml(email, subject, body));
        }
    }
}