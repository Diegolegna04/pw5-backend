package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.service.AuthService;
import it.webdev.pw5.itsincom.service.SessionService;
import it.webdev.pw5.itsincom.service.UserService;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api/users")
public class UserResource {

    @Inject
    SessionService sessionService;
    @Inject
    UserService userService;
    @Inject
    AuthService authService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@CookieParam("SESSION_COOKIE") String token) throws UserNotFound, SessionNotFound {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing session token").build();
        }

        ObjectId userId = sessionService.findUserByToken(token);

        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid session token").build();
        }

        User u = authService.findUserById(userId);

        if (u == null || u.getRole() != User.Role.ADMIN) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized").build();
        }
        return Response.ok(userService.getAllUsers()).build();
    }

}
