package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.rest.model.BookingRequest;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.service.BookingService;
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
    public Response createBooking(Booking booking) {
        bookingService.createBooking(booking);
        return Response.ok("Prenotazione effettuata").build();
    }

    @PUT
    @Path("/accept/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptBooking(@PathParam("id") ObjectId id) {
        Booking updatedBooking = bookingService.acceptBooking(id);
        return Response.ok(updatedBooking).build();
    }

    @PUT
    @Path("/cancel/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("id") ObjectId id) {
        bookingService.cancelBooking(id);
        return Response.ok("Prenotazione cancellata").build();
    }
}