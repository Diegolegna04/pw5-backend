package it.webdev.pw5.itsincom.rest.model;

import io.quarkus.security.spi.runtime.AuthenticationSuccessEvent;
import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.Session;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.rest.AuthService;
import it.webdev.pw5.itsincom.service.EventService;
import it.webdev.pw5.itsincom.service.SessionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.jboss.logging.annotations.Pos;

import java.util.List;

@Path("/api/events")
public class EventResource {
    @Inject
    EventService eventService;
    SessionService sessionService;
    AuthService authService;

    @GET
    public Response getAllEvents() {
        return Response.ok(eventService.getAllEvents()).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(@CookieParam("SESSION_COOKIE") String token, Event event) {

        // Controlla se il token è presente
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing session token").build();
        }

        // Verifica esistenza della sessione dell'utente
        ObjectId userId = sessionService.findUtenteByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        // Verifica che l'utente sia autorizzato a creare l'evento
        User user = authService.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not found").build();
        }

        if (!user.getRole().equals(User.Role.HOSTING_COMPANY)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to create events").build();
        }

        // Convalida dei dati dell'evento
        if (event == null || event.getTitle() == null || event.getDate() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid event data").build();
        }

        // Aggiungi l'evento
        try {
            boolean conferma = eventService.addEvent(event);
            if (conferma) {
                return Response.ok("Event created successfully").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to create event").build();
            }
        } catch (Exception e) {
            // Registra l'errore per scopi di debugging
            e.printStackTrace(); // Sostituisci con un logger come SLF4J o JUL in produzione
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }


}
