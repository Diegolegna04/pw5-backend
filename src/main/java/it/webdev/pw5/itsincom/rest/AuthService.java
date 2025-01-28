package it.webdev.pw5.itsincom.rest;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.repository.AuthRepository;
import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.SessionRepository;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.rest.model.RegisterRequest;
import it.webdev.pw5.itsincom.service.SessionService;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@ApplicationScoped
public class AuthService {

    private final AuthRepository authRepository;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;

    public AuthService(AuthRepository authRepository, SessionService sessionService, SessionRepository sessionRepository) {
        this.authRepository = authRepository;
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public Response registerUser(RegisterRequest req) throws EmailNotAvailable, EmptyField {
        // Check if there are any empty fields
        checkEmptyFields(req);

        // Check if the inserted email is already present in DB
        if (authRepository.findByEmail(req.getEmail()) != null) {
            throw new EmailNotAvailable();
        }

        // TODO: sposta la creazione dell'user nella UserRepository
        // Create the new user
        User user = new User();
        user.setName(req.getEmail());
        user.setEmail(req.getEmail());
        user.setPassword(authRepository.hashPassword(req.getPassword()));
        // TODO: imposta la scelta dei ruoli

        // Persist it
        authRepository.persist(user);

        return Response.ok().build();
    }

    @Transactional
    public Response loginUser(LoginRequest req) throws EmptyField, WrongEmailOrPassword, LoginNotPossible {
        // Check if there are any empty fields
        checkEmptyFields(req);

        // Check if the inserted email exists in DB
        User user = authRepository.findByEmail(req.getEmail()); // TODO: spostare il metodo in UserRepository
        if (user == null) {
            throw new WrongEmailOrPassword();
        }

        // Check credentials and get the userId (necessary for creating his session)
        ObjectId userId = authRepository.checkCredentials(req);
        if (userId == null) {
            throw new WrongEmailOrPassword();
        }

        // TODO: se l'utente elimina il cookie ma ha la sessione attiva bisogna reimpostarlo?
        // Check if the user already has a session
        Session existingSession = sessionRepository.findSessionByUserId(userId);
        if (existingSession != null) {
            throw new LoginNotPossible();
        }

        Session userSession = sessionService.createAndPersistSession(userId);

        // If everything is correct, return a successful response
        return Response.ok("Login effettuato, sessione creata correttamente").
                cookie(new NewCookie.Builder("SESSION_COOKIE")
                        .value(userSession.getToken())
                        .path("/")
                        .build())
                .build();
    }

    @Transactional
    public void logoutUser(String sessionCookie) throws SessionNotFound {
        sessionService.deleteSession(sessionCookie);
    }


    // Check if any of the fields in both LoginRequest and RegisterRequest are empty
    private void checkEmptyFields(Object req) throws EmptyField {
        if (req instanceof LoginRequest loginReq) {
            if (loginReq.getEmail() == null || loginReq.getEmail().trim().isEmpty()) {
                throw new EmptyField();
            }
            if (loginReq.getPassword() == null || loginReq.getPassword().trim().isEmpty()) {
                throw new EmptyField();
            }
        } else if (req instanceof RegisterRequest registerReq) {
            if (registerReq.getEmail() == null || registerReq.getEmail().trim().isEmpty()) {
                throw new EmptyField();
            }
            if (registerReq.getPassword() == null || registerReq.getPassword().trim().isEmpty()) {
                throw new EmptyField();
            }
            if (registerReq.getName() == null || registerReq.getName().trim().isEmpty()) {
                throw new EmptyField();
            }
        } else {
            throw new IllegalArgumentException("Request type is not supported");
        }
    }

    // TODO: sposta in userRepository
    public User findById(ObjectId userId) {
        return authRepository.findById(userId);
    }
}
