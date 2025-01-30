package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Booking;

import java.util.Date;

public class BookingResponse {
    private Date eventDate;
    private Booking.Status status;
    private String title;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Booking.Status getStatus() {
        return status;
    }

    public void setStatus(Booking.Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
