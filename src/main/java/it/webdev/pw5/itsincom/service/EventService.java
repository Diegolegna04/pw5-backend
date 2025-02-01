package it.webdev.pw5.itsincom.service;

import io.quarkus.panache.common.Sort;
import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import it.webdev.pw5.itsincom.rest.model.EventResponse;
import it.webdev.pw5.itsincom.rest.model.PagedListResponse;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@ApplicationScoped
public class EventService {

    @Inject
    EventRepository eventRepository;
    @Inject
    UserService userService;

    public PagedListResponse<EventResponse> getAllEvents(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }

        Date currentDate = new Date();
        List<Event> events = eventRepository.findAllEvents(page, size);
        List<EventResponse> eventResponses = events.stream()
                .map(event -> {
                    EventResponse response = toEventResponse(event);
                    if (event.getDate().before(currentDate)) {
                        response.setFilter(EventResponse.Filter.PAST);
                    } else {
                        response.setFilter(EventResponse.Filter.UPCOMING);
                    }
                    return response;
                })
                .collect(Collectors.toList());

        long totalCount = eventRepository.countAllEvents();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        return new PagedListResponse<>(eventResponses, page, size, totalCount, totalPages);
    }

    public Event getEventById(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        Event event = eventRepository.findById(id);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        return event;
    }

    public void addEvent(String token, Event event) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }
        if (event == null || event.getTitle() == null || event.getDate() == null) {
            throw new IllegalArgumentException("The event in input is invalid");
        }
        eventRepository.saveEvent(event);
    }

    public void updateEvent(String token, ObjectId id, Event event) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }
        Event existingEvent = getEventById(id);
        if (existingEvent == null) {
            throw new IllegalArgumentException("Event not found");
        }

        updateFieldIfNotNull(existingEvent::setTitle, event.getTitle());
        updateFieldIfNotNull(existingEvent::setDate, event.getDate());
        updateFieldIfNotNull(existingEvent::setLocation, event.getLocation());
        updateFieldIfNotNull(existingEvent::setType, event.getType());
        updateFieldIfNotNull(existingEvent::setMaxParticipants, event.getMaxParticipants());
        updateFieldIfNotNull(existingEvent::setParticipants, event.getParticipants());
        updateFieldIfNotNull(existingEvent::setHostingCompanies, event.getHostingCompanies());
        updateFieldIfNotNull(existingEvent::setSpeakers, event.getSpeakers());
        updateFieldIfNotNull(existingEvent::setFilter, event.getFilter());

        eventRepository.updateEvent(existingEvent);
    }

    private <T> void updateFieldIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public void deleteEvent(String token, ObjectId id) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }

        Event event = getEventById(id);
        event.delete();
    }

    public PagedListResponse<EventResponse> filterEventsByYear(int year, int page, int size) {
        try {
            if (page < 1 || size < 1) {
                throw new IllegalArgumentException("Page and size must be greater than 0");
            }

            // Recupera gli eventi filtrati per anno con paginazione
            List<Event> events = eventRepository.findAllFilteredEvents(year, page, size);

            // Converte gli eventi in EventResponse
            List<EventResponse> eventResponses = events
                    .stream()
                    .map(this::toEventResponse)
                    .collect(Collectors.toList());

            // Recupera il numero totale di eventi filtrati per anno
            long totalCount = eventRepository.countFilteredEventsByYear(year);

            // Calcola il numero totale di pagine
            int totalPages = (int) Math.ceil((double) totalCount / size);

            return new PagedListResponse<>(eventResponses, page, size, totalCount, totalPages);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching events: " + e.getMessage());
            throw new RuntimeException("Failed to fetch events", e);
        }
    }


    private EventResponse toEventResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId().toString());
        response.setDate(event.getDate());
        response.setType(event.getType());
        response.setTitle(event.getTitle());
        response.setLocation(event.getLocation());
        response.setParticipantCount(event.getParticipants().size());
        response.setMaxParticipants(event.getMaxParticipants());
        response.setHostingCompanies(event.getHostingCompanies());
        response.setSpeakers(event.getSpeakers());
        return response;
    }
}
