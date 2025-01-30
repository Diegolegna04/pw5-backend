package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.UserUpdated;
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
    SessionService sessionService;
    @Inject
    AuthService authService;

    @Inject
    HashCalculator hashCalculator;

    public List<User> getAllUsers(String token) throws SessionNotFound, UserNotFound {
        if (token == null) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        ObjectId id = sessionService.findUserByToken(token);
        User u = userRepository.findUserById(id);
        if (u == null) {
            throw new UserNotFound();
        }

        if (!u.getRole().equals(User.Role.ADMIN)) {
            throw new IllegalArgumentException("User is autoraized to perform this action");
        }
        return userRepository.listAll();
    }

    public User getUserById(ObjectId id) throws UserNotFound {
        if (id == null) {
            throw new UserNotFound();
        }
        return userRepository.findUserById(id);
    }


    public void updateUser(String token, ObjectId userIdToUpdate, UserUpdated updatedUser) throws SessionNotFound, UserNotFound, UserUnauthorized {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Session token is missing");
        }

        // Trova l'utente autenticato dalla sessione
        ObjectId authenticatedUserId = sessionService.findUserByToken(token);
        if (authenticatedUserId == null) {
            throw new SessionNotFound();
        }

        User authenticatedUser = userRepository.findUserById(authenticatedUserId);
        if (authenticatedUser == null) {
            throw new UserNotFound();
        }

        // Trova l'utente da aggiornare
        User userToUpdate = userRepository.findUserById(userIdToUpdate);
        if (userToUpdate == null) {
            throw new UserNotFound();
        }

        // Aggiorna solo i campi non nulli
        if (updatedUser.getName() != null) {
            userToUpdate.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            userToUpdate.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            userToUpdate.setPassword(hashCalculator.hash(updatedUser.getPassword()));
        }

        // Salva le modifiche
        userRepository.update(userToUpdate);
    }
}
