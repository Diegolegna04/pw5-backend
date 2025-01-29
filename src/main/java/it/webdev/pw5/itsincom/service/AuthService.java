package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.repository.AuthRepository;
import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.SessionRepository;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.rest.model.RegisterRequest;
import it.webdev.pw5.itsincom.service.SessionService;
import it.webdev.pw5.itsincom.service.exception.*;
import it.webdev.pw5.itsincom.service.exception.EmailNotAvailable;
import it.webdev.pw5.itsincom.service.exception.EmptyField;
import it.webdev.pw5.itsincom.service.exception.LoginNotPossible;
import it.webdev.pw5.itsincom.service.exception.WrongEmailOrPassword;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void registerUser(RegisterRequest req) throws EmailNotAvailable{
        // Check if the inserted email is already present in DB
        if (authRepository.findByEmail(req.getEmail()) != null) {
            throw new EmailNotAvailable();
        }

        // Create the new user
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(authRepository.hashPassword(req.getPassword()));
        user.setRole(req.getRole());

        // Persist it
        authRepository.persist(user);
    }

    @Transactional
    public Session loginUser(LoginRequest req) throws WrongEmailOrPassword, LoginNotPossible {
        // Check if the inserted email exists in DB
        User user = authRepository.findByEmail(req.getEmail());
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

        return sessionService.createAndPersistSession(userId);
    }

    @Transactional
    public void logoutUser(String sessionCookie) throws SessionNotFound {
        sessionService.deleteSession(sessionCookie);
    }


    // Check if any of the fields in both LoginRequest and RegisterRequest are empty
    public void checkEmptyFields(Object req) throws EmptyField {
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

    public void checkEmailFormat(Object req) throws InvalidEmailFormat {
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (req instanceof LoginRequest loginReq) {
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(loginReq.getEmail());
            if (!matcher.matches()){
                throw new InvalidEmailFormat();
            }
        } else if (req instanceof RegisterRequest registerReq) {
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(registerReq.getEmail());
            if (!matcher.matches()){
                throw new InvalidEmailFormat();
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
