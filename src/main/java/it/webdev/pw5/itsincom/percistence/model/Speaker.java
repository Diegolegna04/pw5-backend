package it.webdev.pw5.itsincom.percistence.model;

import java.util.List;

public class Speaker {

    private String name;
    private String topic;
    private List<String> tags;

    // No-argument constructor (for frameworks)
    public Speaker() {}

    // Constructor with parameters
    public Speaker(String name, String topic, List<String> tags) {
        this.name = name;
        this.topic = topic;
        this.tags = tags;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
