package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.percistence.model.Event;
import it.webdev.pw5.itsincom.service.EventService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/events")
public class EventResource {
    @Inject
    EventService eventService;

    @GET
    public Response getAllEvents() {
        return Response.ok(eventService.getAllEvents()).build();
    }

}
