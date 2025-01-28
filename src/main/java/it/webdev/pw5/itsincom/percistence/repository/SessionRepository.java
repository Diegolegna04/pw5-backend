package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.Session;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.sql.Timestamp;
import java.util.UUID;

@ApplicationScoped
public class SessionRepository implements PanacheMongoRepository<Session> {

    public Session createAndPersistSession(ObjectId userId){
        Session s = new Session();
        s.setCreationDate(new Timestamp(System.currentTimeMillis()));
        s.setUserId(userId);
        s.setToken(UUIDGenerator());
        persist(s);
        return s;
    }

    private String UUIDGenerator() {
        return UUID.randomUUID().toString();
    }
}
