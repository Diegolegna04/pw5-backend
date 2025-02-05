package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.Partner;
import it.webdev.pw5.itsincom.percistence.model.Speaker;

import java.util.Date;
import java.util.List;

public class EventResponse {
    private String id;
    private Date date;
    private String type;
    private String title;
    private String location;
    private int participantCount;
    private Filter filter;
    private Integer maxParticipants;
    private List<Partner> partners;
    private List<Speaker> speakers;

    public enum Filter {
        UPCOMING, PAST
    }

    public static EventResponse mapEventToEventResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId().toString());
        response.setDate(event.getDate());
        response.setType(event.getType());
        response.setTitle(event.getTitle());
        response.setLocation(event.getLocation());
        response.setParticipantCount(event.getParticipants() != null ? event.getParticipants().size() : 0);
        response.setMaxParticipants(event.getMaxParticipants());
        response.setPartners(event.getPartners());
        response.setSpeakers(event.getSpeakers());
        if (event.getFilter() != null) {
            response.setFilter(EventResponse.Filter.valueOf(event.getFilter().name()));
        }

        return response;
    }

    public EventResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<Partner> getPartners() {
        return partners;
    }

    public void setPartners(List<Partner> partners) {
        this.partners = partners;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }
}
