package it.webdev.pw5.itsincom.service;

import com.google.zxing.WriterException;
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
import org.bson.types.ObjectId;
import java.io.IOException;
import java.util.List;


@ApplicationScoped
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final PdfService pdfService;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, UserService userService, EmailService emailService, EventRepository eventRepository, PdfService pdfService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.eventRepository = eventRepository;
        this.pdfService = pdfService;
    }

    public List<BookingResponse> getPagedBookings(int page, int size) {
        return bookingRepository.findAll().page(page - 1, size)
                .list().stream().map(this::toBookingResponse).toList();
    }

    public List<Booking> getAllBookingsByEventID(ObjectId eventId) {
        return bookingRepository.findBookingsByEventId(eventId);
    }

    public void createBooking(String token, Booking booking) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);
        if (booking.getEventId() == null) {
            throw new IllegalArgumentException("EventId is required");
        }
        booking.setUserId(user.getId());
        if (!user.getRole().equals(User.Role.USER)) {
            throw new UserUnauthorized();
        }
        if (bookingRepository.checkDoubleBooking(user.getId(), booking.getEventId())) {
            throw new IllegalArgumentException("This event has already been booked by the user");
        }

        Event event = eventRepository.findEventById(booking.getEventId());
/*        if (event.getParticipants().size() >= event.getMaxParticipants()) {
            throw new IllegalArgumentException("The event has reached the maximum number of participants");
        }*/
        event.addParticipant(user.getId());
        eventRepository.persistOrUpdate(event);

        bookingRepository.saveBooking(booking);


        String userEmail = user.getEmail();
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


    public void acceptBooking(String token, ObjectId bookingId) throws UserNotFound, UserUnauthorized, SessionNotFound, IOException, WriterException {
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
        if (token == null) {
            throw new SessionNotFound();
        }

        User user = userService.findUserByToken(token);
        if (user == null) {
            throw new UserNotFound();
        }

        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Prenotazione non trovata con ID: " + bookingId);
        }

        Event event = eventRepository.findEventById(booking.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Evento non trovato per la prenotazione: " + bookingId);
        }

        // 1️⃣ Rimuove l'utente dalla lista dei partecipanti dell'evento
        eventRepository.removeParticipant(user, event);
        eventRepository.update(event);

        // 2️⃣ Cancella la prenotazione
        bookingRepository.cancelBooking(bookingId);

        // 3️⃣ Controlla se ci sono prenotazioni in PENDING per l'evento e accetta la prima disponibile
        List<Booking> pendingBookings = bookingRepository.findPendingBookingsByEventId(event.getId());
        if (!pendingBookings.isEmpty()) {
            Booking nextBooking = pendingBookings.get(0); // Prende la prima prenotazione in attesa
            bookingRepository.acceptBooking(nextBooking.getId());
        }
    }


    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setEventDate(booking.getEventDate());
        response.setTitle(booking.getTitle());
        response.setStatus(booking.getStatus());
        response.setEventId(booking.getEventId().toString());
        return response;
    }

    public void acceptAllBookings(String token, ObjectId id) throws UserNotFound, SessionNotFound, UserUnauthorized {
        User user = userService.findUserByToken(token);

        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }

        Event e = eventRepository.findEventById(id);
        if (e == null) {
            throw new IllegalArgumentException("Evento non trovato con ID: " + id);
        }

        int maxParticipants = e.getMaxParticipants();
        List<Booking> bookings = getAllBookingsByEventID(id);

        if (bookings.isEmpty()) {
            System.out.println("Nessuna prenotazione disponibile per questo evento.");
            return;
        }

        int toAccept = Math.min(maxParticipants, bookings.size());

        for (int i = 0; i < toAccept; i++) {
            bookingRepository.acceptBooking(bookings.get(i).getId());
        }
    }
}