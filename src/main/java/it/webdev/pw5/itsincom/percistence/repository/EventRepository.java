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

//    public List<Event> findAllEvents() {
//
//    }

    public List<Event> findAllFilteredEvents(int year, int page, int size) {
        return find("year(date) = ?1", year)
                .page(page - 1, size)
                .list();
    }
    public long countFilteredEventsByYear(int year) {
        return count("year(date) = ?1", year);
    }
}
