package it.webdev.pw5.itsincom;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")

public class Home {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String h() {
        return "Hello from Quarkus REST";
    }
}
