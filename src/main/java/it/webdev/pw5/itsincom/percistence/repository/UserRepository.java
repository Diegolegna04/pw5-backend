package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;
@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {
    public List<User> getAllUsers() {
        return listAll();
    }

    public User findUserById(ObjectId id) {
        return findById(id);
    }

    public User findUserByEmail(String email) {
        return find("email", email).firstResult();
    }
}
