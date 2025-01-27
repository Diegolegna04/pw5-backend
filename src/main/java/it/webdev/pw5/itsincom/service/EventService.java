package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EventService {
    @Inject
    EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.getAllEvents();
    }
}
