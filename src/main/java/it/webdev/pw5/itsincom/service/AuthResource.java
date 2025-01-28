package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.rest.AuthService;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.rest.model.RegisterRequest;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import javax.sound.midi.Soundbank;

@Path("/api/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterRequest req) throws EmailNotAvailable, EmptyField {
        return authService.registerUser(req);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(LoginRequest req) throws EmptyField, LoginNotPossible, WrongEmailOrPassword {
        return authService.loginUser(req);
    }

    @DELETE
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutUser(@CookieParam("SESSION_COOKIE") String sessionCookie) throws SessionNotFound {
        try {
            if (sessionCookie == null){
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
