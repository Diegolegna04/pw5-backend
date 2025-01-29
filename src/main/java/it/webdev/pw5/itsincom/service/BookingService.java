package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.repository.BookingRepository;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BookingService {
    @Inject
    BookingRepository bookingRepository;

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.getAllBookings().stream().map(this::toBookingResponse).toList();
    }

    public void createBooking(Booking booking) {
        bookingRepository.saveBooking(booking);
    }

    public BookingResponse findBookingById(ObjectId id) {
        return toBookingResponse(bookingRepository.findBookingById(id));
    }

    public List<BookingResponse> findBookingsByUserId(int userId) {
        return bookingRepository.findBookingsByUserId(userId).stream().map(this::toBookingResponse).toList();
    }

    public Booking acceptBooking(ObjectId bookingId) {
        return bookingRepository.acceptBooking(bookingId);
    }

    public BookingResponse cancelBooking(ObjectId bookingId) {
        return toBookingResponse(bookingRepository.cancelBooking(bookingId));
    }

    private BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setName(booking.getName());
        response.setEventDate(booking.getEventDate());
        response.setStatus(booking.getStatus());
        return response;
    }


}
