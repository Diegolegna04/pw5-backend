package it.webdev.pw5.itsincom.percistence.model;

public class EventFilters {
    private Integer year;
    private String speaker;
    private String type;

    public EventFilters() {

    }
    public EventFilters(Integer year, String speaker, String type) {
        this.year = year;
        this.speaker = speaker;
        this.type = type;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
