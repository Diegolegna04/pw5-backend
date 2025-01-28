package it.webdev.pw5.itsincom.percistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@MongoEntity(collection = "session")
public class Session extends PanacheMongoEntity {
    private ObjectId id;
    private String token;
    private ObjectId userId;
    private LocalDateTime creationDate;

    public Session() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
