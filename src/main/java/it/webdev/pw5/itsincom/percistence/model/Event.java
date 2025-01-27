package it.webdev.pw5.itsincom.percistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@MongoEntity(collection = "event")
public class Event extends PanacheMongoEntity {

    private ObjectId id;
    private Date date;
    private String type;
    private String title;
    private String location;
    private List<String> hostingCompanies;
    private List<Speaker> speakers;
    private List<String> topics;
    private List<String> guests;
    private List<User> participants;

    public Event() {}

    public Event(String title, String type, List<String> topics, Date date, String location, List<String> guests,
                 List<String> hostingCompanies, List<User> participants, List<Speaker> speakers) {
        this.title = title;
        this.type = type;
        this.topics = topics;
        this.date = date;
        this.location = location;
        this.guests = guests;
        this.hostingCompanies = hostingCompanies;
        this.participants = participants;
        this.speakers = speakers;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public List<String> getHostingCompanies() {
        return hostingCompanies;
    }

    public void setHostingCompanies(List<String> hostingCompanies) {
        this.hostingCompanies = hostingCompanies;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}
