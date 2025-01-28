package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.repository.SessionRepository;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;


@ApplicationScoped
public class SessionService {

    @Inject
    SessionRepository sessionRepository;

    public Session createAndPersistSession(ObjectId userId) {
        return sessionRepository.createAndPersistSession(userId);
    }

    public ObjectId findUserByToken(String token) {
        return sessionRepository.findUtenteByToken(token);
    }

    public void deleteSession(String token) throws SessionNotFound {
        Session s = sessionRepository.findSessionByCookie(token);
        sessionRepository.deleteSession(s);
    }

    public boolean checkSession(String token) {
        return sessionRepository.checkSession(token);
    }
}
