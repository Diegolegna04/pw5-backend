package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.repository.SessionRepository;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;


@ApplicationScoped
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }


    public Session createAndPersistSession(ObjectId userId) {
        return sessionRepository.createAndPersistSession(userId);
    }

    public void deleteSession(String token) throws SessionNotFound {
        Session s = sessionRepository.findSessionByCookie(token);
        sessionRepository.deleteSession(s);
    }

    public ObjectId findUtenteByToken(String token) {
    public ObjectId findUsereByToken(String token) {
        return sessionRepository.findUtenteByToken(token);
    }

    public boolean checkSession(String tooken) {
        return sessionRepository.checkSession(tooken);
    }
}
