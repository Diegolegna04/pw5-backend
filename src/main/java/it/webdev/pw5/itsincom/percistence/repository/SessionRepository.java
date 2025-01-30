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
        delete(s);
    }

    public ObjectId findUserByToken(String token) throws SessionNotFound {
        Session s = find("token", token).firstResult();
        if (s == null) {
            throw new SessionNotFound();
        }
        return s.getUserId();
    }

    public Session findSessionByCookie(String token) throws SessionNotFound {
        Session session = find("token", token).firstResult();
        if (session == null) {
            throw new SessionNotFound();
        }
        return session;
    }

    public Session findSessionByUserId(ObjectId userId) throws SessionNotFound {
        return find("userId", userId).firstResult();
    }


    public String UUIDGenerator() {
        return UUID.randomUUID().toString();
    }
}
