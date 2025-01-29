package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.BookingRequest;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.service.AuthService;
import it.webdev.pw5.itsincom.service.BookingService;
import it.webdev.pw5.itsincom.service.SessionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/bookings")
public class BookingResource {

    @Inject
    BookingService bookingService;

    @Inject
    AuthService authService;

    @Inject
    SessionService sessionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public BookingResponse getBookingById(@PathParam("id") ObjectId id) {
        return bookingService.findBookingById(id);
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BookingResponse> getBookingsByUserId(@PathParam("id") int userId) {
        return bookingService.findBookingsByUserId(userId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(@CookieParam("SESSION_COOKIE") String token, Booking booking) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing session token").build();
        }

        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        User user = authService.findById(userId);
        if (!user.getRole().equals(User.Role.HOSTING_COMPANY) || !user.getRole().equals(User.Role.USER)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to create events").build();
        }

        bookingService.createBooking(booking);
        return Response.ok(booking).build();
    }

    @PUT
    @Path("/accept/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptBooking(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing session token").build();
        }

        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        User user = authService.findById(userId);
        if (!user.getRole().equals(User.Role.HOSTING_COMPANY) || !user.getRole().equals(User.Role.USER)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to create events").build();
        }
        Booking updatedBooking = bookingService.acceptBooking(id);
        return Response.ok(updatedBooking).build();
    }

    @PUT
    @Path("/cancel/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing session token").build();
        }

        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        User user = authService.findById(userId);
        if (!user.getRole().equals(User.Role.HOSTING_COMPANY) || !user.getRole().equals(User.Role.USER)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to create events").build();
        }
        BookingResponse updatedBooking = bookingService.cancelBooking(id);
        return Response.ok(updatedBooking).build();
    }
}