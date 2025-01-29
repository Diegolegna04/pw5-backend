package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import it.webdev.pw5.itsincom.rest.model.EventResponse;
import it.webdev.pw5.itsincom.rest.model.PagedListResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventService {

    @Inject
    SessionService sessionService;
    @Inject
    AuthService authService;
    @Inject
    EventRepository eventRepo;


    public PagedListResponse<EventResponse> getAllEvents(int page, int size) {
        try {
            List<EventResponse> eventResponses = eventRepo.findAll().page(page - 1, size)
                    .list().stream().map(this::toEventResponse).collect(Collectors.toList());
            long totalCount = eventRepo.count();
            int totalPages = (int) Math.ceil((double) totalCount / size);
            return new PagedListResponse<>(eventResponses, page, size, totalCount, totalPages);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching all events: " + e.getMessage());
            throw new RuntimeException("Failed to fetch all events", e);
        }
    }

    public Event getEventById(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        Event event = eventRepo.findById(id);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        return event;
    }

    public void addEvent(String token, Event event) {
        ObjectId userId = sessionService.validateSession(token);
        User user = authService.findById(userId);

        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new SecurityException("You do not have permission to create events");
        }

        if (event == null || event.getTitle() == null || event.getDate() == null) {
            throw new IllegalArgumentException("Invalid event data");
        }

        event.persist();
    }

    public void updateEvent(String token, ObjectId id, Event event) {
        ObjectId userId = sessionService.validateSession(token);
        User user = authService.findById(userId);

        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new SecurityException("You do not have permission to update events");
        }

        Event existingEvent = getEventById(id);

        if (event.getTitle() != null) existingEvent.setTitle(event.getTitle());
        if (event.getDate() != null) existingEvent.setDate(event.getDate());
        if (event.getLocation() != null) existingEvent.setLocation(event.getLocation());
        if (event.getHostingCompanies() != null) existingEvent.setHostingCompanies(event.getHostingCompanies());
        if (event.getSpeakers() != null) existingEvent.setSpeakers(event.getSpeakers());
        if (event.getParticipants() != null) existingEvent.setParticipants(event.getParticipants());

        existingEvent.persist();
    }

    public void deleteEvent(String token, ObjectId id) {
        ObjectId userId = sessionService.validateSession(token);
        User user = authService.findById(userId);

        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new SecurityException("You do not have permission to delete events");
        }

        Event event = getEventById(id);
        event.delete();
    }

    private EventResponse toEventResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId().toString());
        response.setDate(event.getDate());
        response.setType(event.getType());
        response.setTitle(event.getTitle());
        response.setLocation(event.getLocation());
        response.setParticipantCount(event.getParticipants().size());
        return response;
    }
}
