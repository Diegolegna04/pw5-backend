package it.webdev.pw5.itsincom.rest;

import it.webdev.pw5.itsincom.percistence.model.Partner;
import it.webdev.pw5.itsincom.rest.model.PartnerRequest;
import it.webdev.pw5.itsincom.service.PartnerService;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.Base64;
import java.util.List;


@Path("/api/partner")
public class PartnerResource {

    private final PartnerService partnerService;

    public PartnerResource(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPartners() {
        List<Partner> partners = partnerService.getAllPartners();
        return Response.ok().entity(partners).build();
    }

    @GET
    @Path("/image/{id}")
    @Produces("image/png")
    public Response getPartnerImage(@PathParam("id") ObjectId imageId) {
        byte[] imageData = partnerService.getPartnerImage(imageId);
        String base64String = Base64.getEncoder().encodeToString(imageData);
        String partnerImage = "data:image/png;base64," + base64String;
        return Response.ok(partnerImage).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPartner(@CookieParam("SESSION_COOKIE") String token, PartnerRequest p) throws UserNotFound, UserUnauthorized, SessionNotFound, PartnerAlreadyExists, XSSAttackAttempt {
        partnerService.createPartner(token, p);
        return Response.ok().build();
    }
}
