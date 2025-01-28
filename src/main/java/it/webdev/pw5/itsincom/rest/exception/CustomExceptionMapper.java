package it.webdev.pw5.itsincom.rest.exception;

import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof EmailNotAvailable) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("This email is already in use.")
                    .type("text/plain")
                    .build();
        } else if (e instanceof EmptyField) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Please fill in all required fields.")
                    .type("text/plain")
                    .build();
        } else if (e instanceof WrongEmailOrPassword) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Email or password are incorrect")
                    .type("text/plain")
                    .build();
        } else if (e instanceof LoginNotPossible) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A session is already active for this user. Please log out before trying again.")
                    .type("text/plain")
                    .build();
        } else if (e instanceof SessionNotFound) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Session not found")
                    .type("text/plain")
                    .build();
        } else if (e instanceof InvalidEmailFormat) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email format is invalid")
                    .type("text/plain")
                    .build();
        }


        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("An unexpected error occurred: " + e.getMessage())
                .type("text/plain")
                .build();
    }
}
