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
    String imageBase64;
    String websiteURL;

    public Partner(){

    }
    public Partner(ObjectId id, String name, String description, String number, String location, String imageBase64, String websiteURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.number = number;
        this.location = location;
        this.imageBase64 = imageBase64;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }
}
