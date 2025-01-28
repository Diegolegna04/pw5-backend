package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.service.AuthService;
import it.webdev.pw5.itsincom.service.SessionService;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api/auth")
public class AuthResource {

    @Inject
    AuthService authService;
    SessionService sessionService;


    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterRequest req) throws EmailNotAvailable, EmptyField {
        return authService.registerUser(req);
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest req) throws EmptyField, LoginNotPossible, WrongEmailOrPassword {
        return authService.loginUser(req);
    }

    @Path("/checkSession")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkSession(@CookieParam("SESSION_COOKIE") String token) {
        // Verifica se il token è presente e non vuoto
        if (token == null || token.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Session token is missing or empty").build();
        }

        // Verifica la validità della sessione
        try {
            boolean isValid = sessionService.checkSession(token);
            if (isValid) {
                return Response.ok()
                        .entity("Session is valid").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Session is invalid or expired").build();
            }
        } catch (Exception e) {
            // Log dell'eccezione (sostituire con un logger in produzione)
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred while verifying the session").build();
        }
    }


    @Path("/checkCategory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkCategory(@CookieParam("SESSION_COOKIE") String token) {
        if (token != null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing session token").build();
        }

        try {
            ObjectId userId = sessionService.findUserByToken(token);
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid or expired session token").build();
            }

            User user = authService.findById(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found").build();
            }

            return Response.ok().entity(user.getRole()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred while verifying the session").build();
        }
    }

    @DELETE
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutUser(@CookieParam("SESSION_COOKIE") String sessionCookie) throws SessionNotFound {
        try {
            if (sessionCookie == null) {
                throw new IllegalArgumentException("Session cookie is empty");
            }
            authService.logoutUser(sessionCookie);
            NewCookie newCookie = new NewCookie.Builder("SESSION_COOKIE").value("")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .secure(false)
                    .build();
            return Response.ok("Logout succeeded").cookie(newCookie).build();
        } catch (Exception e) {
            throw new SessionNotFound();
        }
    }
}
