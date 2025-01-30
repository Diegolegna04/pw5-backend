package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.rest.model.RegisterRequest;
import it.webdev.pw5.itsincom.service.AuthService;
import it.webdev.pw5.itsincom.service.SessionService;
import it.webdev.pw5.itsincom.service.UserService;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;
    @Inject
    SessionService sessionService;
    @Inject
    UserService userService;


    @Path("/register")
    @POST
    public Response registerUser(@Valid RegisterRequest req) throws EmailNotAvailable {
        authService.registerUser(req);
        return Response.ok().entity("Registration completed successfully").build();
    }

    @GET
    @Path("/verify")
    public Response verifyEmail(@QueryParam("token") String emailVerificationToken) throws UserNotFound {
        authService.verifyEmail(emailVerificationToken);
        return Response.ok().entity("Email verified").build();
    }

    @Path("/login")
    @POST
    public Response login(@Valid LoginRequest req) throws WrongEmailOrPassword, SessionNotFound, UserIsNotVerified {
        Session s = authService.loginUser(req);
        return Response.ok("Login succeeded").
                cookie(new NewCookie.Builder("SESSION_COOKIE").value(s.getToken()).path("/").build())
                .build();
    }

    @Path("/check-session")
    @GET
    public Response checkIfUserHasActiveSession(@CookieParam("SESSION_COOKIE") String token) throws CookieIsNull, SessionNotFound {
        if (token == null || token.isBlank()) {
            throw new CookieIsNull();
        }
        sessionService.checkSession(token);
        return Response.ok().entity("Session is valid").build();
    }


    @Path("/check-role")
    @GET
    public Response checkUserRole(@CookieParam("SESSION_COOKIE") String token) throws CookieIsNull, SessionNotFound, UserNotFound {
        if (token == null || token.isBlank()) {
            throw new CookieIsNull();
        }
        ObjectId userId = sessionService.findUserByToken(token);
        User user = userService.getUserById(userId);
        return Response.ok().entity(user.getRole()).build();
    }

    @DELETE
    @Path("/logout")
    public Response logoutUser(@CookieParam("SESSION_COOKIE") String token) throws CookieIsNull, SessionNotFound {
        if (token == null || token.isBlank()) {
            throw new CookieIsNull();
        }
        authService.logoutUser(token);
        NewCookie newCookie = new NewCookie.Builder("SESSION_COOKIE").value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .build();
        return Response.ok().entity("Logout succeeded").cookie(newCookie).build();
    }
}
