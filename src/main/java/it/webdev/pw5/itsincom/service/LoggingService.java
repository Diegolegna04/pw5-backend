package it.webdev.pw5.itsincom.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LoggingService {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingService.class);

    public void performAction() {
        LOG.info("Azione eseguita con successo.");
        LOG.debug("Dettagli aggiuntivi per il debug.");
        LOG.error("Errore rilevato.");
    }
}

