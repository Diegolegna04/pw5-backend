package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Sort;
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

    public List<Event> findAllEvents(int page, int size) {
        return findAll(Sort.by("date").descending())
                .page(page - 1, size)
                .list();
    }

    public long countAllEvents() {
        return count();
    }

    public Event findEventById(ObjectId id) {
        return findById(id);
    }

    public List<Event> findAllFilteredEvents(int year, int page, int size) {
        return find("year(date) = ?1", year)
                .page(page - 1, size)
                .list();
    }

    public long countFilteredEventsByYear(int year) {
        return count("year(date) = ?1", year);
    }

    public void updateEvent(Event event) {
        this.persistOrUpdate(event);
    }

    public void removeParticipant(User user, Event event) {
        event.getParticipants().remove(user.getId());
        this.persistOrUpdate(event);
    }
}
