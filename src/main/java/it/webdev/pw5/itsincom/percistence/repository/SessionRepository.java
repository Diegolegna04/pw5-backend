package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.service.exception.LoginNotPossible;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class SessionRepository implements PanacheMongoRepository<Session> {

    public Session createAndPersistSession(ObjectId userId) {
        Session s = new Session();
        s.setCreationDate(LocalDateTime.now());
        s.setUserId(userId);
        s.setToken(UUIDGenerator());
        persist(s);
        return s;
    }

    public void deleteSession(Session s) {
        if (s == null) {
            throw new IllegalArgumentException("Session cannot be null");    // TODO: crea una nuova eccezione
        }

        delete(s);
    }

    public ObjectId findUtenteByToken(String token) {
        Session s = find("token", token).firstResult();
        if (s != null) {
            return s.getUserId();
        }
        return null;

    }

    public Session findSessionByCookie(String sessionCookie) throws SessionNotFound {
        try {
            return find("token", sessionCookie).firstResult();
        } catch (Exception e) {
            throw new SessionNotFound();
        }
    }

    public Session findSessionByUserId(ObjectId userId) throws LoginNotPossible {
        try {
            return find("userId", userId).firstResult();
        } catch (Exception e) {
            throw new LoginNotPossible();
        }
    }


    private String UUIDGenerator() {
        return UUID.randomUUID().toString();
    }
}
