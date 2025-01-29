package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.BookingRepository;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BookingService {
    @Inject
    BookingRepository bookingRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    EmailService emailService;

    @Inject
    AuthService authService;

    @Inject
    SessionService sessionService;

    public List<BookingResponse> getAllBookings() {
        try {
            return bookingRepository.getAllBookings().stream().map(this::toBookingResponse).toList();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching all bookings: " + e.getMessage());
            throw new RuntimeException("Failed to fetch all bookings", e);
        }
    }

    public void createBooking(Booking booking) {
        try {
            if (booking.getEventId() == null) {
                throw new IllegalArgumentException("EventId is required");
            }
            if (booking.getUserId() == null) {
                throw new IllegalArgumentException("UserId is required");
            }

            bookingRepository.saveBooking(booking);

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

    public List<BookingResponse> findBookingsByUserId(int userId) {
        try {
            return bookingRepository.findBookingsByUserId(userId).stream().map(this::toBookingResponse).toList();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching bookings by user ID: " + e.getMessage());
            throw new RuntimeException("Failed to fetch bookings by user ID", e);
        }
    }

    public Booking acceptBooking(ObjectId bookingId) {
        try {
            return bookingRepository.acceptBooking(bookingId);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while accepting booking: " + e.getMessage());
            throw new RuntimeException("Failed to accept booking", e);
        }
    }

    public BookingResponse cancelBooking(ObjectId bookingId) {
        try {
            return toBookingResponse(bookingRepository.cancelBooking(bookingId));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while canceling booking: " + e.getMessage());
            throw new RuntimeException("Failed to cancel booking", e);
        }
    }

    public void validateUserPermissions(String token, User.Role requiredRole) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Missing session token");
        }

        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid or expired session token");
        }

        User user = authService.findById(userId);
        if (!user.getRole().equals(requiredRole)) {
            throw new IllegalArgumentException("You do not have permission to perform this action");
        }
    }

    private BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setName(booking.getName());
        response.setEventDate(booking.getEventDate());
        response.setStatus(booking.getStatus());
        return response;
    }
}