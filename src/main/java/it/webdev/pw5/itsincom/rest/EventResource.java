package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.percistence.model.EventFilters;
import it.webdev.pw5.itsincom.rest.model.EventResponse;
import it.webdev.pw5.itsincom.rest.model.PagedListResponse;
import it.webdev.pw5.itsincom.service.EventService;
import it.webdev.pw5.itsincom.service.exception.SessionNotFound;
import it.webdev.pw5.itsincom.service.exception.UserNotFound;
import it.webdev.pw5.itsincom.service.exception.UserUnauthorized;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;


@Path("/api/events")
public class EventResource {

    private final EventService eventService;

    public EventResource(EventService eventService){
        this.eventService = eventService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilteredEvents(@QueryParam("page") @DefaultValue("1") int page,
                                      @QueryParam("size") @DefaultValue("10") int size,
                                      EventFilters filters) {
        PagedListResponse<EventResponse> response = eventService.getFilteredEvents(page, size, filters);
        return Response.ok(response).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUpcomingEvents(){
        PagedListResponse<EventResponse> response = eventService.getUpcomingEvents();
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("id") ObjectId id) {
        Event event = eventService.getEventById(id);
        return Response.ok(event).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(@CookieParam("SESSION_COOKIE") String token, Event event) throws UserNotFound, SessionNotFound, UserUnauthorized {
        eventService.addEvent(token, event);
        return Response.ok("{\"message: \": \"Event created successfully\"}").build();
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id, Event event) throws UserNotFound, SessionNotFound, UserUnauthorized {
        eventService.updateEvent(token, id, event);
        return Response.ok("{\"message\": \"Event updated successfully\"}").build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@CookieParam("SESSION_COOKIE") String token, @PathParam("id") ObjectId id) throws UserNotFound, SessionNotFound, UserUnauthorized {
        eventService.deleteEvent(token, id);
        return Response.ok("{\"message\": \"Event deleted successfully\"}").build();
    }
}
