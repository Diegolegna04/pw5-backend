package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.rest.AuthService;
import it.webdev.pw5.itsincom.rest.model.LoginRequest;
import it.webdev.pw5.itsincom.rest.model.RegisterRequest;
import it.webdev.pw5.itsincom.service.exception.EmailNotAvailable;
import it.webdev.pw5.itsincom.service.exception.EmptyField;
import it.webdev.pw5.itsincom.service.exception.LoginNotPossible;
import it.webdev.pw5.itsincom.service.exception.WrongEmailOrPassword;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

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
}
