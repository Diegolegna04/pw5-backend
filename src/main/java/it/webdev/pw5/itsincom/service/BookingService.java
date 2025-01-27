package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.repository.BookingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BookingService {
    @Inject
    BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.getAllBookings();
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.saveBooking(booking);
    }

    public Booking findBookingById(ObjectId id) {
        return bookingRepository.findBookingById(id);
    }

    public List<Booking> findBookingsByUserId(int userId) {
        return bookingRepository.findBookingsByUserId(userId);
    }

    public Booking acceptBooking(ObjectId bookingId) {
        return bookingRepository.acceptBooking(bookingId);
    }

    public Booking cancelBooking(ObjectId bookingId) {
        return bookingRepository.cancelBooking(bookingId);
    }


}
