package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.listAll();
    }


}
