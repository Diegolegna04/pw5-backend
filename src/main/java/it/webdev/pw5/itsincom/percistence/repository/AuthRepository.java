package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.service.HashCalculator;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import org.bson.types.ObjectId;

@ApplicationScoped
public class AuthRepository implements PanacheMongoRepository<User> {

    private final HashCalculator hashCalculator;

    public AuthRepository(HashCalculator hashCalculator) {
        this.hashCalculator = hashCalculator;
    }

    // Find a user by email
    // TODO: spostare in userRepository?
    public User findUserByEmail(String email) {
        return find("email", email).firstResult();
    }

    public User findUserByVerificationToken(String emailVerificationToken) throws UserNotFound {
        User u = find("verificationToken", emailVerificationToken).firstResult();
        if (u == null){
            throw new UserNotFound();
        }
        return u;
    }

    // Check if the credentials while logging in inserted are correct
    public ObjectId checkCredentials(String email, String password){
        User u = find("email", email).firstResult();

        // Check if the user exists
        if (u == null) {
            return null;
        }

        // Check if the hashed psw is correct
        if (hashPassword(password).equals(u.getPassword())) {
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
