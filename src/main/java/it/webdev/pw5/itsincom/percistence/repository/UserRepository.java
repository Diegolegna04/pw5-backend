package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;


@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {

    @Inject
    SessionRepository sessionRepository;

    public User findUserById(ObjectId id) {
        return findById(id);
    }

    public User findUserByEmail(String email) {
        return find("email", email).firstResult();
    }

    public User findUserByToken(String token) throws SessionNotFound, UserNotFound {
        ObjectId userId = sessionRepository.findUserIdByToken(token);
        User u = findById(userId);
        if (u == null) {
            throw new UserNotFound();
        }
        return u;
    }
}
