package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.webdev.pw5.itsincom.percistence.model.Event;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import javax.sql.DataSource;
import java.util.List;

@ApplicationScoped
public class EventRepository implements PanacheMongoRepositoryBase<Event, ObjectId> {

    public void saveEvent(Event event) {
        this.persist(event);
    }

    public Event findEventById(ObjectId id) {
        return findById(id);
    }

}
