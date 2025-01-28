package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.service.HashCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

@ApplicationScoped
public class AuthRepository implements PanacheMongoRepository<User> {

    private final HashCalculator hashCalculator;

    public AuthRepository(HashCalculator hashCalculator) {
        this.hashCalculator = hashCalculator;
    }

    // Find a user by email
    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    // Check if the credentials while logging in inserted are correct
    public ObjectId checkCredentials(LoginRequest req){
        User u = find("email", req.getEmail()).firstResult();

        // Check if the user exists
        if (u == null) {
            return null;
        }

        // Check if the hashed psw is correct
        if (hashPassword(req.getPassword()).equals(u.getPassword())) {
            return u.getId();
        } else {
            return null;
        }
    }

    // Hash the password
    public String hashPassword(String password) {
        return hashCalculator.hash(password);
    }
}
