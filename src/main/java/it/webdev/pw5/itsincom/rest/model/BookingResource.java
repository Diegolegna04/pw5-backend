package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/api/bookings")
public class BookingResource {

    @Inject
    BookingService bookingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Booking getBookingById(ObjectId id) {
        return bookingService.findBookingById(id);
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingService.findBookingsByUserId(userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Booking createBooking(Booking booking) {
        return bookingService.createBooking(booking);
    }

    @POST
    @Path("/accept/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Booking acceptBooking(ObjectId id) {
        return bookingService.acceptBooking(id);
    }

    @POST
    @Path("/cancel/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Booking cancelBooking(ObjectId id) {
        return bookingService.cancelBooking(id);
    }

}
