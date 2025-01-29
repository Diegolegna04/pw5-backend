package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.rest.model.EventResponse;
import it.webdev.pw5.itsincom.rest.model.PagedListResponse;
import it.webdev.pw5.itsincom.service.EventService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/api/events")
public class EventResource {

    @Inject
    EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEvents(@QueryParam("page") @DefaultValue("1") int page,
                                 @QueryParam("size") @DefaultValue("10") int size) {
        PagedListResponse<EventResponse> response = eventService.getAllEvents(page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("id") ObjectId id) {
        Event event = eventService.getEventById(id);
        return Response.ok(event).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(@CookieParam("SESSION_COOKIE") String token, Event event) {
        eventService.addEvent(token, event);
        return Response.ok("Event created successfully").build();
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id, Event event) {
        eventService.updateEvent(token, id, event);
        return Response.ok("Event updated successfully").build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) {
        eventService.deleteEvent(token, id);
        return Response.ok("Event deleted successfully").build();
    }
}
