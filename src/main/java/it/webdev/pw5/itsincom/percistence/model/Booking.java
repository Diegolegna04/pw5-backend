package it.webdev.pw5.itsincom.percistence.model;

import java.util.Date;

public class Booking {
    private String id;
    private String userId;
    private String eventId;
    private Status status;
    private Date date;

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    public Booking() {
    }

    public Booking(String userId, String eventId, Status status, Date date) {
        this.userId = userId;
        this.eventId = eventId;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
