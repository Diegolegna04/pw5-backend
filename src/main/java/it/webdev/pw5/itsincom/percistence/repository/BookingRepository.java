package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.webdev.pw5.itsincom.percistence.model.Booking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class BookingRepository implements PanacheMongoRepositoryBase<Booking, ObjectId> {

    @Inject
    EventRepository eventRepository;

    @Inject
    UserRepository userRepository;


    public List<Booking> getAllBookings() {
        return listAll();
    }

    private Date getEventDate(Booking booking) {
        return eventRepository.findEventById(booking.getEventId()).getDate();
    }

    public void saveBooking(Booking booking) {
        booking.setName(userRepository.findUserById(booking.getUserId()).getName());
        booking.setStatus(Booking.Status.PENDING);
        booking.setEventDate(getEventDate(booking));
        this.persist(booking);
    }

    public Booking findBookingById(ObjectId id) {
        return findById(id);
    }

    public List<Booking> findBookingsByUserId(int userId) {
        return list("id_utente", userId);
    }

    public void acceptBooking(ObjectId bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking != null) {
            booking.setStatus(Booking.Status.ACCEPTED);
            persistOrUpdate(booking);
        }
    }

    public void cancelBooking(ObjectId bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking != null) {
            booking.setStatus(Booking.Status.REJECTED);
            persistOrUpdate(booking);
        }
    }
}
