package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/api/bookings")
public class BookingResource {

    @Inject
    BookingService bookingService;

    @GET
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }





}
