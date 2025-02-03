package it.webdev.pw5.itsincom.rest.model;

public class PartnerRequest {
    String name;
    String description;
    String number;
    String location;
    String imageBase64;
    String websiteURL;

    public PartnerRequest(){

    }
    public PartnerRequest(String name, String description, String number, String location, String imageBase64, String websiteURL) {
        this.name = name;
        this.description = description;
        this.number = number;
        this.location = location;
        this.imageBase64 = imageBase64;
        this.websiteURL = websiteURL;
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
