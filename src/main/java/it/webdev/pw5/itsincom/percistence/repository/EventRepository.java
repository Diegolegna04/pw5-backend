package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.webdev.pw5.itsincom.percistence.model.Event;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import javax.sql.DataSource;
import java.util.List;

@ApplicationScoped
public class EventRepository implements PanacheMongoRepositoryBase<Event, ObjectId> {
    public List<Event> getAllEvents() {
        return listAll();
    }

    public Event saveEvent(Event event) {
        this.persist(event);
        return event;
    }

    public Event findEventById(ObjectId id) {
        return findById(id);
    }

    public List<Event> findEventsByUserId(int userId) {
        return list("id_utente", userId);
    }


}
