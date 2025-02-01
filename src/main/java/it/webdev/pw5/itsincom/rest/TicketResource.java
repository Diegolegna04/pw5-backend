package it.webdev.pw5.itsincom.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/tickets")
public class TicketResource {

    @GET
    @Path("/{fileName}")
    @Produces("application/pdf")
    public Response getTicket(@PathParam("fileName") String fileName) {
        try {
            File file = new File("tickets/" + fileName);
            if (!file.exists()) {
                return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
            }
            return Response.ok(Files.readAllBytes(Paths.get(file.getPath())))
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving file").build();
        }
    }
}