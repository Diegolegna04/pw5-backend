package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.Booking;
import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.Ticket;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.BookingRepository;
import it.webdev.pw5.itsincom.percistence.repository.EventRepository;
import it.webdev.pw5.itsincom.percistence.repository.UserRepository;
import it.webdev.pw5.itsincom.rest.model.BookingResponse;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;


@ApplicationScoped
public class BookingService {

    @Inject
    BookingRepository bookingRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    UserService userService;
    @Inject
    EmailService emailService;
    @Inject
    EventRepository eventRepository;
    @Inject
    PdfService pdfService;

    public List<BookingResponse> getAllBookings(int page, int size) {
        return bookingRepository.findAll().page(page - 1, size)
                .list().stream().map(this::toBookingResponse).toList();
    }

    public void createBooking(String token, Booking booking) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);
        if (booking.getEventId() == null) {
            throw new IllegalArgumentException("EventId is required");
        }
        if (booking.getUserId() == null) {
            throw new IllegalArgumentException("UserId is required");
        }
        if (!user.getRole().equals(User.Role.USER)) {
            throw new UserUnauthorized();
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
    }

    public BookingResponse findBookingById(ObjectId id) {
        return toBookingResponse(bookingRepository.findBookingById(id));
    }

    public List<BookingResponse> findBookingsByUserId(ObjectId userId) {
        return bookingRepository.findBookingsByUserId(userId).stream().map(this::toBookingResponse).toList();
    }


    public void acceptBooking(String token, ObjectId bookingId) throws UserNotFound, UserUnauthorized, SessionNotFound, IOException {
        User user = userService.findUserByToken(token);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }

        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking != null) {
            bookingRepository.acceptBooking(bookingId);

            Event event = eventRepository.findEventById(booking.getEventId());
            String userEmail = userRepository.findUserById(booking.getUserId()).getEmail();

            // Generate the ticket
            Ticket ticket = new Ticket();
            ticket.setTicketNumber(booking.getId().hashCode());
            ticket.setTitle(event.getTitle());
            ticket.setEventDate(event.getDate());

            String ticketPath = pdfService.generateTicketPdf(ticket);
            String ticketUrl = "http://localhost:8080/" + ticketPath;
            emailService.sendBookingAccepted(
                    userEmail,
                    "Booking Confirmation",
                    "Your booking has been accepted.",
                    ticketUrl
            );
        }
    }

    public void cancelBooking(String token, ObjectId bookingId) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }
        bookingRepository.cancelBooking(bookingId);
    }

    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setEventDate(booking.getEventDate());
        response.setTitle(booking.getTitle());
        response.setStatus(booking.getStatus());
        return response;
    }
}