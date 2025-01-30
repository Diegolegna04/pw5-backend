package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.User;


public class UserResponse {
    private String name;
    private String email;


    public UserResponse() {
    }

    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();

    }

    public UserResponse(String name, String email, String password) {
        this.name = name;
        this.email = email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}