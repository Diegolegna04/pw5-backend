package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.rest.model.UserResponse;
import it.webdev.pw5.itsincom.rest.model.UserUpdated;
import it.webdev.pw5.itsincom.service.UserService;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/api/user")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@CookieParam("SESSION_COOKIE") String token) throws SessionNotFound, UserNotFound, UserUnauthorized, SessionCookieIsNull {
        List<User> users = userService.getAllUsers(token);
        return Response.ok(users).build();
    }

    @GET
    @Path("/bookings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingsForUser(@CookieParam("SESSION_COOKIE") String token) {
        try {
            List<BookingResponse> bookings = userService.getBookingsForUserByToken(token);
            return Response.ok(bookings).build();
        } catch (UserNotFound | SessionNotFound e) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }

    @GET
    @Path("/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserBySessionCookie(@CookieParam("SESSION_COOKIE") String token) throws UserNotFound, SessionNotFound, XSSAttackAttempt {
        User user = userService.findUserByToken(token);
        UserResponse userResponse = new UserResponse(user);
        userResponse.sanitize();
        return Response.ok(userResponse).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@CookieParam("SESSION_COOKIE") String token, UserUpdated newUser) throws UserNotFound, SessionNotFound, SessionCookieIsNull, XSSAttackAttempt {
        newUser.sanitize();
        userService.updateUser(token, newUser);
        return Response.ok().entity("{\"message\": \"Modifica andata a buon fine\"}").build();
    }
}
