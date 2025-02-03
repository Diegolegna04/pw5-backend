package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.repository.AuthRepository;
import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.SessionRepository;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.rest.model.RegisterRequest;
import it.webdev.pw5.itsincom.service.exception.*;
import it.webdev.pw5.itsincom.service.exception.EmailNotAvailable;
import it.webdev.pw5.itsincom.service.exception.WrongEmailOrPassword;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;


@ApplicationScoped
public class AuthService {

    private final AuthRepository authRepository;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public AuthService(AuthRepository authRepository, SessionService sessionService, SessionRepository sessionRepository, EmailService emailService, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(RegisterRequest req) throws EmailNotAvailable {
        // Check if the inserted email is already present in DB
        if (userRepository.findUserByEmail(req.getEmail()) != null) {
            throw new EmailNotAvailable();
        }
        String token = sessionRepository.UUIDGenerator();
        // Create the new user
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(authRepository.hashPassword(req.getPassword()));
        user.setRole(User.Role.USER);
        user.setStatus(User.Status.NOT_VERIFIED);
        user.setVerificationToken(token);
        // Persist it
        authRepository.persist(user);
        // Send the verification email
        sendVerificationEmail(token, req.getEmail());
    }

    private void sendVerificationEmail(String token, String email) {
        // Build the verification link
        String verificationLink = "http://localhost:4200/verify-account?token=" + token;
        String message = "Clicca sul <a href=\"" + verificationLink + "\">link</a> di verifica per autenticare la tua email";
        // Send verification
        emailService.sendVerificationEmail(email, message);
    }

    @Transactional
    public void verifyEmail(String emailVerificationToken) throws UserNotFound {
        // Find the user that has the verification token sent by email
        User user = authRepository.findUserByVerificationToken(emailVerificationToken);
        // Change user's role from "NOT_VERIFIED" to "USER" and delete the verification token
        user.setStatus(User.Status.VERIFIED);
        user.setVerificationToken(null);
        authRepository.update(user);
    }

    @Transactional
    public Session loginUser(LoginRequest req) throws WrongEmailOrPassword, SessionNotFound, UserIsNotVerified {
        // Check if the inserted email exists in DB
        User user = userRepository.findUserByEmail(req.getEmail());
        if (user == null) {
            throw new WrongEmailOrPassword();
        }
        // Check if the user has verified his email
        boolean isVerified = checkIfUserIsVerified(user);
        if (!isVerified){
            throw new UserIsNotVerified();
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

    private boolean checkIfUserIsVerified(User user){
        return user.getStatus().equals(User.Status.VERIFIED);
    }
}
