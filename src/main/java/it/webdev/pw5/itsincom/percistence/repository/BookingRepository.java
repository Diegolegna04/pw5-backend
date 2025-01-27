package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.webdev.pw5.itsincom.percistence.model.Booking;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BookingRepository implements PanacheMongoRepositoryBase<Booking, ObjectId> {


    public List<Booking> getAllBookings() {
        return listAll();
    }

    public Booking saveBooking(Booking booking) {
        this.persist(booking);
        return booking;
    }

    public Booking findBookingById(ObjectId id) {
        return findById(id);
    }

    public List<Booking> findBookingsByUserId(int userId) {
        return list("id_utente", userId);
    }

    // Update booking status
    public Booking acceptBooking(ObjectId bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking != null) {
            booking.setStatus(Booking.Status.valueOf("ACCEPTED"));
            persistOrUpdate(booking);
        }
        return booking;
    }

    // Delete a booking (if required)
    public Booking cancelBooking(ObjectId bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking != null) {
            booking.setStatus(Booking.Status.valueOf("REJECTED"));
            persistOrUpdate(booking);
        }
        return booking;
    }
}
