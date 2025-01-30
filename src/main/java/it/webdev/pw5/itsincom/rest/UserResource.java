package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.UserResponse;
import it.webdev.pw5.itsincom.rest.model.UserUpdated;
import it.webdev.pw5.itsincom.service.SessionService;
import it.webdev.pw5.itsincom.service.UserService;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/api/users")
public class UserResource {

    @Inject
    SessionService sessionService;
    @Inject
    UserService userService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@CookieParam("SESSION_COOKIE") String token) throws SessionNotFound, UserNotFound {
        List<User> users = userService.getAllUsers(token);
        return Response.ok(users).build();
    }

    @GET
    @Path("/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@CookieParam("SESSION_COOKIE") String token) throws UserNotFound, SessionNotFound {
        ObjectId id = sessionService.findUserByToken(token);
        User user = userService.getUserById(id);
        UserResponse userResponse = new UserResponse(user);
        return Response.ok(userResponse).build();
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id, UserUpdated newUser) throws UserNotFound, SessionNotFound, UserUnauthorized {
        userService.updateUser(token, id, newUser);
        return Response.ok().entity("Modifica andata a buon fine").build();
    }

}
