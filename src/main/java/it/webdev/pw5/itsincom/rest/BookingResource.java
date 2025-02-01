package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.service.BookingService;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.io.IOException;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(@CookieParam("SESSION_COOKIE") String token, Booking booking) throws UserNotFound, SessionNotFound, UserUnauthorized {
        bookingService.createBooking(token, booking);
        return Response.ok(booking).build();
    }

    @PUT
    @Path("/accept/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptBooking(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) throws UserNotFound, UserUnauthorized, SessionNotFound, IOException {
        bookingService.acceptBooking(token, id);
        return Response.ok().entity("booking accepted").build();
    }

    @PUT
    @Path("/cancel/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) throws UserNotFound, UserUnauthorized, SessionNotFound {
        bookingService.cancelBooking(token, id);
        return Response.ok().entity("booking canceled").build();
    }
}