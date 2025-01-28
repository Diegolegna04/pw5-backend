package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class EventService {
    @Inject
    EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.getAllEvents();
    }

    public boolean addEvent(Event event) {
        try {
            eventRepository.saveEvent(event);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Event getEventById(ObjectId id) {
        return eventRepository.findEventById(id);
    }
}
