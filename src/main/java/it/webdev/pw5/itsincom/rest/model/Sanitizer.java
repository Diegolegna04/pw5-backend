package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.service.exception.XSSAttackAttempt;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class Sanitizer {

    public static String sanitize(String input) throws XSSAttackAttempt {
        // Sanitize input
        String sanitizedInput = Jsoup.clean(input, Safelist.basic());
        if (sanitizedInput.isEmpty()) {
            throw new XSSAttackAttempt();
        }
        return sanitizedInput;
    }
}

