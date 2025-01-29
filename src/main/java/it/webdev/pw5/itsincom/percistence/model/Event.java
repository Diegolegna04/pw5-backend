package it.webdev.pw5.itsincom.percistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.util.ArrayList;
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
    private List<ObjectId> participants;
    private Integer maxParticipants;
    private Filter filter;

    public enum Filter {
        UPCOMING, PAST
    }


    public Event() {
        this.participants = new ArrayList<>();
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

    public List<ObjectId> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ObjectId> participants) {
        this.participants = participants;
    }

    public void addParticipant(ObjectId userId) {
        this.participants.add(userId);
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
