package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.service.AuthService;
import it.webdev.pw5.itsincom.service.EventService;
import it.webdev.pw5.itsincom.service.SessionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api/events")
public class EventResource {

    @Inject
    EventService eventService;
    @Inject
    SessionService sessionService;
    @Inject
    AuthService authService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEvents() {
        return Response.ok(eventService.getAllEvents()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("id") ObjectId id) {

        Event event = eventService.getEventById(id);

        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Event not found").build();
        }

        return Response.ok(event).build();
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
        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        User user = authService.findById(userId);

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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id, Event event) {
        // Controlla se il token è presente
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing session token").build();
        }

        // Verifica esistenza della sessione dell'utente
        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        // Verifica che l'utente esista
        User user = authService.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not found").build();
        }

        // Verifica che l'utente abbia il ruolo appropriato
        if (!user.getRole().equals(User.Role.HOSTING_COMPANY)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to update events").build();
        }

        // Verifica che l'ID dell'evento sia valido
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid event ID").build();
        }

        // Recupera l'evento esistente
        Event existingEvent = eventService.findById(id);
        if (existingEvent == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Event not found").build();
        }

        // Aggiorna i campi dell'evento
        try {
            eventService.updateEvent(event, existingEvent);
            return Response.ok("Event updated successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while updating the event").build();
        }
    }


    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) {
        // Controlla se il token è presente
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing session token").build();
        }

        // Verifica esistenza della sessione dell'utente
        ObjectId userId = sessionService.findUserByToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired session token").build();
        }

        // Verifica che l'utente esista
        User user = authService.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not found").build();
        }

        // Verifica che l'utente abbia il ruolo appropriato
        if (!user.getRole().equals(User.Role.HOSTING_COMPANY)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to delete events").build();
        }

        // Verifica che l'ID dell'evento sia valido
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid event ID").build();
        }

        // Recupera l'evento esistente
        Event existingEvent = eventService.findById(id);
        if (existingEvent == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Event not found").build();
        }

        // Cancella l'evento
        try {
            eventService.deleteEvent(existingEvent);
            return Response.ok("Event deleted successfully").build();
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while deleting the event").build();
        }
    }


}
