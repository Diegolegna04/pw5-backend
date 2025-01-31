package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.UserUpdated;
import it.webdev.pw5.itsincom.service.exception.SessionCookieIsNull;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import java.util.List;


@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;
    @Inject
    HashCalculator hashCalculator;

    public List<User> getAllUsers(String token) throws SessionNotFound, UserNotFound, UserUnauthorized, SessionCookieIsNull {
        if (token == null || token.isEmpty()) {
            throw new SessionCookieIsNull();
        }
        User u = findUserByToken(token);
        if (!u.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }
        return userRepository.listAll();
    }

    public User getUserById(ObjectId id) throws UserNotFound {
        if (id == null) {
            throw new UserNotFound();
        }
        return userRepository.findUserById(id);
    }

    public void updateUser(String token, UserUpdated updatedUser) throws SessionNotFound, UserNotFound, SessionCookieIsNull {
        if (token == null || token.isEmpty()) {
            throw new SessionCookieIsNull();
        }
        // Find user by session
        User user = findUserByToken(token);
        // Update the user with the new info
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(hashCalculator.hash(updatedUser.getPassword()));
        }
        userRepository.update(user);
    }

    public User findUserByToken(String token) throws UserNotFound, SessionNotFound {
        return userRepository.findUserByToken(token);
    }
}
