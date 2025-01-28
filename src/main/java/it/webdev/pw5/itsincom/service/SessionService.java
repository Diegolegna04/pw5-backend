package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.repository.SessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;


@ApplicationScoped
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService (SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }


    public Session createAndPersistSession(ObjectId userId) {
        return sessionRepository.createAndPersistSession(userId);
    }

    public ObjectId findUsereByToken(String token) {
        return sessionRepository.findUtenteByToken(token);
    }
}
