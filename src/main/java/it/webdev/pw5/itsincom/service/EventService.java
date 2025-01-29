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
        eventRepository.saveEvent(event);
        return true;
    }

    public Event getEventById(ObjectId id) {
        return eventRepository.findEventById(id);
    }

    public void updateEvent(Event event, Event existingEvent) {
        if (event == null) {
            throw new IllegalArgumentException("Event data cannot be null");
        }

        // Aggiorna i campi solo se presenti nel nuovo evento
        if (event.getTitle() != null) {
            existingEvent.setTitle(event.getTitle());
        }
        if (event.getDate() != null) {
            existingEvent.setDate(event.getDate());
        }
        if (event.getLocation() != null) {
            existingEvent.setLocation(event.getLocation());
        }
        if (event.getHostingCompanies() != null) {
            existingEvent.setHostingCompanies(event.getHostingCompanies());
        }
        if (event.getSpeakers() != null) {
            existingEvent.setSpeakers(event.getSpeakers());
        }
        if (event.getParticipants() != null) {
            existingEvent.setParticipants(event.getParticipants());
        }

        if (event.getMaxParticipants() != null) {
            existingEvent.setMaxParticipants(event.getMaxParticipants());
        }

        // Salva le modifiche
        existingEvent.update();
    }


    public Event findById(ObjectId id) {
        return eventRepository.findById(id);
    }

    public void deleteEvent(Event existingEvent) {
        existingEvent.delete();
    }
}
