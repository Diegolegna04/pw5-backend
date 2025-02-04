package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;


@ApplicationScoped
public class EventRepository implements PanacheMongoRepositoryBase<Event, ObjectId> {

    public void saveEvent(Event event) {
        this.persist(event);
    }

    public Event findEventById(ObjectId id) {
        return findById(id);
    }

    public void updateEvent(Event event) {
        this.persistOrUpdate(event);
    }

    public void removeParticipant(User user, Event event) {
        event.getParticipants().remove(user.getId());
        this.persistOrUpdate(event);
    }

    public List<Event> getAllEvents(){
        return listAll();
    }
}
