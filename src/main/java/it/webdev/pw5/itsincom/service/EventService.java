package it.webdev.pw5.itsincom.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.EventFilters;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.EventResponse;
import it.webdev.pw5.itsincom.rest.model.PagedListResponse;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@ApplicationScoped
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserService userService, EmailService emailService, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
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

        String changesSummary = generateEventChangesSummary(existingEvent, event);

        updateFieldIfNotNull(existingEvent::setTitle, event.getTitle());
        updateFieldIfNotNull(existingEvent::setDate, event.getDate());
        updateFieldIfNotNull(existingEvent::setLocation, event.getLocation());
        updateFieldIfNotNull(existingEvent::setType, event.getType());
        updateFieldIfNotNull(existingEvent::setMaxParticipants, event.getMaxParticipants());
        updateFieldIfNotNull(existingEvent::setPartners, event.getPartners());
        updateFieldIfNotNull(existingEvent::setSpeakers, event.getSpeakers());
        updateFieldIfNotNull(existingEvent::setFilter, event.getFilter());

        eventRepository.updateEvent(existingEvent);

        // Fetch email addresses of participants
        List<String> participantEmails = (existingEvent.getParticipants() != null)
                ? existingEvent.getParticipants().stream()
                .map(userRepository::findUserById)
                .filter(Objects::nonNull)
                .map(User::getEmail)
                .toList()
                : new ArrayList<>();

        String subject = "Event Updated: " + existingEvent.getTitle();
        String body = "The event you are participating in has been updated. Please check the details.\n\n" + changesSummary;
        emailService.sendUpdateEmailToParticipants(participantEmails, subject, body);
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

    public PagedListResponse<EventResponse> getFilteredEvents(int page, int size, EventFilters filters) {
        // Build the query
        StringBuilder queryBuilder = new StringBuilder();
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        Date currentDate = new Date();

        // 1. Filter for year
        if (filters.getYear() != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(filters.getYear(), Calendar.JANUARY, 1, 0, 0, 0);
            Date startDate = cal.getTime();
            cal.set(filters.getYear() + 1, Calendar.JANUARY, 1, 0, 0, 0);
            Date endDate = cal.getTime();

            conditions.add("\"date\": { \"$gte\": ?1, \"$lt\": ?2 }");
            params.add(startDate);
            params.add(endDate);
        }

        // 2. Filter for speaker
        if (filters.getSpeaker() != null && !filters.getSpeaker().trim().isEmpty()) {
            conditions.add("\"speakers.name\": ?".concat(String.valueOf(params.size() + 1)));
            params.add(filters.getSpeaker().trim());
        }

        // 3. Filter for type
        if (filters.getType() != null && !filters.getType().trim().isEmpty()) {
            conditions.add("\"type\": ?".concat(String.valueOf(params.size() + 1)));
            params.add(filters.getType().trim());
        }

        // Combine filters with $and
        if (!conditions.isEmpty()) {
            queryBuilder.append("{ \"$and\": [ ");
            queryBuilder.append(conditions.stream().map(c -> "{ " + c + " }").collect(Collectors.joining(", ")));
            queryBuilder.append(" ] }");
        }

        // Build the query
        PanacheQuery<Event> query;
        if (!conditions.isEmpty()) {
            query = eventRepository.find(queryBuilder.toString(), params.toArray());
        } else {
            query = eventRepository.findAll();
        }

        // Paginate the response
        List<Event> events = query.list();

        for (Event event : events) {
            if (event.getDate().after(currentDate)) {
                event.setFilter(Event.Filter.UPCOMING);
            } else {
                event.setFilter(Event.Filter.PAST);
            }
        }

        events.sort(Comparator.comparing(Event::getDate).reversed());
        long total = events.size();

        int fromIndex = Math.min((page - 1) * size, events.size());
        int toIndex = Math.min(fromIndex + size, events.size());
        List<Event> paginatedEvents = events.subList(fromIndex, toIndex);

        List<EventResponse> eventResponses = paginatedEvents.stream()
                .map(EventResponse::mapEventToEventResponse)
                .collect(Collectors.toList());

        PagedListResponse<EventResponse> response = new PagedListResponse<>();
        response.setTotalItems(total);
        response.setItems(eventResponses);
        return response;
    }

    public PagedListResponse<EventResponse> getUpcomingEvents(){
        Date currentDate = new Date();
        List<Event> events = eventRepository.getAllEvents();
        List<Event> allUpcomingEvents = events.stream()
                .filter(event -> event.getDate().after(currentDate))
                .sorted(Comparator.comparing(Event::getDate))
                .toList();
        List<Event> upcomingEvents = allUpcomingEvents.subList(0, 3);

        List<EventResponse> eventResponses = upcomingEvents.stream()
                .map(EventResponse::mapEventToEventResponse)
                .collect(Collectors.toList());

        PagedListResponse<EventResponse> response = new PagedListResponse<>();
        response.setTotalItems(eventResponses.size());
        response.setItems(eventResponses);
        return response;
    }

    private String generateEventChangesSummary(Event oldEvent, Event newEvent) {
        StringBuilder changes = new StringBuilder("The following changes were made to the event:\n");

        if (!oldEvent.getTitle().equals(newEvent.getTitle())) {
            changes.append("Title: ").append(oldEvent.getTitle()).append(" -> ").append(newEvent.getTitle()).append("\n");
        }
        if (!oldEvent.getDate().equals(newEvent.getDate())) {
            changes.append("Date: ").append(oldEvent.getDate()).append(" -> ").append(newEvent.getDate()).append("\n");
        }
        if (!oldEvent.getLocation().equals(newEvent.getLocation())) {
            changes.append("Location: ").append(oldEvent.getLocation()).append(" -> ").append(newEvent.getLocation()).append("\n");
        }
        if (!oldEvent.getType().equals(newEvent.getType())) {
            changes.append("Type: ").append(oldEvent.getType()).append(" -> ").append(newEvent.getType()).append("\n");
        }
        if (!oldEvent.getMaxParticipants().equals(newEvent.getMaxParticipants())) {
            changes.append("Max Participants: ").append(oldEvent.getMaxParticipants()).append(" -> ").append(newEvent.getMaxParticipants()).append("\n");
        }
        if (!oldEvent.getPartners().equals(newEvent.getPartners())) {
            changes.append("Hosting Companies: ").append(oldEvent.getPartners()).append(" -> ").append(newEvent.getPartners()).append("\n");
        }
        if (!oldEvent.getSpeakers().equals(newEvent.getSpeakers())) {
            changes.append("Speakers: ").append(oldEvent.getSpeakers()).append(" -> ").append(newEvent.getSpeakers()).append("\n");
        }

        return changes.toString();
    }
}