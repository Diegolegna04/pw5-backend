package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/api/bookings")
public class BookingResource {

    @Inject
    BookingService bookingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BookingResponse> getAllBookings(@QueryParam("page") @DefaultValue("1") int page,
                                                @QueryParam("size") @DefaultValue("10") int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        return bookingService.getAllBookings(page, size);
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
        try {
            bookingService.validateUserPermissions(token, User.Role.USER);
            bookingService.createBooking(booking);
            return Response.ok(booking).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create booking").build();
        }
    }

    @PUT
    @Path("/accept/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptBooking(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) {
        try {
            bookingService.validateUserPermissions(token, User.Role.ADMIN);
            Booking updatedBooking = bookingService.acceptBooking(id);
            return Response.ok(updatedBooking).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to accept booking").build();
        }
    }

    @PUT
    @Path("/cancel/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) {
        try {
            bookingService.validateUserPermissions(token, User.Role.ADMIN);
            BookingResponse updatedBooking = bookingService.cancelBooking(id);
            return Response.ok(updatedBooking).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to cancel booking").build();
        }
    }
}