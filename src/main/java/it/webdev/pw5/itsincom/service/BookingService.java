package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.BookingRepository;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookingService {
    @Inject
    BookingRepository bookingRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    AuthService authService;

    @Inject
    SessionService sessionService;

    @Inject
    EmailService emailService;

    @Inject
    EventRepository eventRepository;

    public List<BookingResponse> getAllBookings(int page, int size) {
        try {
            return bookingRepository.findAll().page(page - 1, size)
                    .list().stream().map(this::toBookingResponse).toList();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching all bookings: " + e.getMessage());
            throw new RuntimeException("Failed to fetch all bookings", e);
        }
    }

    public void createBooking(String token, Booking booking) throws UserNotFound, SessionNotFound {
        ObjectId userId = sessionService.validateSession(token);
        User user = authService.findUserById(userId);
        try {
            if (booking.getEventId() == null) {
                throw new IllegalArgumentException("EventId is required");
            }
            if (booking.getUserId() == null) {
                throw new IllegalArgumentException("UserId is required");
            }
            if (!user.getRole().equals(User.Role.USER)) {
                throw new SecurityException("You do not have permission to create events");
            }
            if (bookingRepository.checkDoubleBooking(booking.getUserId(), booking.getEventId())) {
                throw new IllegalArgumentException("This event has already been booked");
            }

            bookingRepository.saveBooking(booking);

            Event event = eventRepository.findEventById(booking.getEventId());
            event.addParticipant(booking.getUserId());
            eventRepository.persistOrUpdate(event);

            String userEmail = userRepository.findUserById(booking.getUserId()).getEmail();
            emailService.sendBookingConfirmation(
                    userEmail,
                    "Booking Confirmation",
                    "Your booking has been created."
            );
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Failed to create booking", e);
        }
    }

    public BookingResponse findBookingById(ObjectId id) {
        try {
            return toBookingResponse(bookingRepository.findBookingById(id));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching booking by ID: " + e.getMessage());
            throw new RuntimeException("Failed to fetch booking by ID", e);
        }
    }

    public List<BookingResponse> findBookingsByUserId(ObjectId userId) {
        try {
            return bookingRepository.findBookingsByUserId(userId).stream().map(this::toBookingResponse).toList();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching bookings by user ID: " + e.getMessage());
            throw new RuntimeException("Failed to fetch bookings by user ID", e);
        }
    }

    public void acceptBooking(String token, ObjectId bookingId) throws UserNotFound, SessionNotFound {
        ObjectId userId = sessionService.validateSession(token);
        User user = authService.findUserById(userId);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new SecurityException("You do not have permission to create events");
        }
        try {
            bookingRepository.acceptBooking(bookingId);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while accepting booking: " + e.getMessage());
            throw new RuntimeException("Failed to accept booking", e);
        }
    }

    public void cancelBooking(String token, ObjectId bookingId) throws UserNotFound, SessionNotFound {
        ObjectId userId = sessionService.validateSession(token);
        User user = authService.findUserById(userId);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new SecurityException("You do not have permission to cancel events");
        }
        try {
            bookingRepository.cancelBooking(bookingId);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while canceling booking: " + e.getMessage());
            throw new RuntimeException("Failed to cancel booking", e);
        }
    }

    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setEventDate(booking.getEventDate());
        response.setTitle(booking.getTitle());
        response.setStatus(booking.getStatus());
        return response;
    }
}