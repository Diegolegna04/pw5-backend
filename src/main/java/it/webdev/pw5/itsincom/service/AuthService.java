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

import java.util.Optional;
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
    public void registerUser(RegisterRequest req) throws EmailNotAvailable {
        // Check if the inserted email is already present in DB
        if (authRepository.findUserByEmail(req.getEmail()) != null) {
            throw new EmailNotAvailable();
        }

        // Create the new user
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(authRepository.hashPassword(req.getPassword()));
        user.setRole(User.Role.USER);

        // Persist it
        authRepository.persist(user);
    }

    @Transactional
    public Session loginUser(LoginRequest req) throws WrongEmailOrPassword, SessionNotFound {
        // Check if the inserted email exists in DB
        // TODO: spostare in userRepository?
        User user = authRepository.findUserByEmail(req.getEmail());
        if (user == null) {
            throw new WrongEmailOrPassword();
        }
        // Check credentials and get the userId (necessary for creating his session)
        ObjectId userId = authRepository.checkCredentials(req.getEmail(), req.getPassword());
        if (userId == null) {
            throw new WrongEmailOrPassword();
        }
        // Check if the user already has a session
        Session existingSession = sessionRepository.findSessionByUserId(userId);
        // If user already has a session delete it and then create a new one
        if (existingSession != null) {
            sessionService.deleteSession(existingSession.getToken());
        }
        return sessionService.createAndPersistSession(userId);
    }

    @Transactional
    public void logoutUser(String token) throws SessionNotFound {
        sessionService.deleteSession(token);
    }
}
