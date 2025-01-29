package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import org.bson.types.ObjectId;

import java.util.Date;

public class BookingResponse {
    private String name;
    private Date eventDate;
    private Booking.Status status;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
