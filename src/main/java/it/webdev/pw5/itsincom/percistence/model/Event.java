package it.webdev.pw5.itsincom.percistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@MongoEntity(collection = "event")
public class Event extends PanacheMongoEntity {
    private ObjectId id;
    private String title;
    private List<String> topics;
    private Date date;
    private String city;
    private List<String> guests; //infrastrutture degli eventi
    private ObjectId eventOwner;
    private List<User> partecipants;
    private List<User> speakers;

    public Event() {
    }

    public Event(String title, List<String> topics, Date date, String city, List<String> guests, ObjectId eventOwner, List<User> partecipants, List<User> speakers) {
        this.title = title;
        this.topics = topics;
        this.date = date;
        this.city = city;
        this.guests = guests;
        this.eventOwner = eventOwner;
        this.partecipants = partecipants;
        this.speakers = speakers;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }

    public ObjectId getEventOwner() {
        return eventOwner;
    }

    public void setEventOwner(ObjectId eventOwner) {
        this.eventOwner = eventOwner;
    }

    public List<User> getPartecipants() {
        return partecipants;
    }

    public void setPartecipants(List<User> partecipants) {
        this.partecipants = partecipants;
    }

    public List<User> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<User> speakers) {
        this.speakers = speakers;
    }
}
