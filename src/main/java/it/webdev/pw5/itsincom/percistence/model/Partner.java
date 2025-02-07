package it.webdev.pw5.itsincom.percistence.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;


@MongoEntity(collection = "partner")
public class Partner {
    ObjectId id;
    String name;
    String description;
    String number;
    String location;
    ObjectId imageId;
    String websiteURL;

    public Partner(){

    }
    public Partner(ObjectId id, String name, String description, String number, String location, ObjectId imageId, String websiteURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.number = number;
        this.location = location;
        this.imageId = imageId;
        this.websiteURL = websiteURL;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ObjectId getImageId() {
        return imageId;
    }

    public void setImageId(ObjectId imageId) {
        this.imageId = imageId;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }
}
