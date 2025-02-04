package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.service.exception.XSSAttackAttempt;

public class PartnerRequest {
    private String name;
    private String description;
    private String number;
    private String location;
    private String imageBase64;
    private String websiteURL;

    public void sanitizer() throws XSSAttackAttempt {
        this.name = Sanitizer.sanitize(this.name);
        this.description = Sanitizer.sanitize(this.description);
        this.number = Sanitizer.sanitize(this.number);
        this.location = Sanitizer.sanitize(this.location);
        this.imageBase64 = Sanitizer.sanitize(this.imageBase64);
        this.websiteURL = Sanitizer.sanitize(this.websiteURL);
    }

    public PartnerRequest() {

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
