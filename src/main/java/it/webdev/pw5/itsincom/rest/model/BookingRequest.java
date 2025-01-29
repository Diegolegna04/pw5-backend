package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import org.bson.types.ObjectId;

public class BookingRequest {
    private ObjectId userId;
    private ObjectId eventId;

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getEventId() {
        return eventId;
    }

    public void setEventId(ObjectId eventId) {
        this.eventId = eventId;
    }

}
