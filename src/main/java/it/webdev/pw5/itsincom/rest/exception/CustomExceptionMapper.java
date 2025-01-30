package it.webdev.pw5.itsincom.rest.exception;

import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        return switch (e.getClass().getSimpleName()) {
            case "EmailNotAvailable" -> Response.status(Response.Status.CONFLICT)
                    .entity("This email is already in use.")
                    .type("text/plain")
                    .build();
            case "EmptyField" -> Response.status(Response.Status.BAD_REQUEST)
                    .entity("Please fill in all required fields.")
                    .type("text/plain")
                    .build();
            case "WrongEmailOrPassword" -> Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Email or password are incorrect")
                    .type("text/plain")
                    .build();
            case "LoginNotPossible" -> Response.status(Response.Status.CONFLICT)
                    .entity("A session is already active for this user. Please log out before trying again.")
                    .type("text/plain")
                    .build();
            case "SessionNotFound" -> Response.status(Response.Status.NOT_FOUND)
                    .entity("Session not found")
                    .type("text/plain")
                    .build();
            case "InvalidEmailFormat" -> Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email format is invalid")
                    .type("text/plain")
                    .build();
            case "CookieIsNull" -> Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cookie is null")
                    .type("text/plain")
                    .build();
            case "UserNotFound" -> Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .type("text/plain")
                    .build();
            default -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error: " + e.getMessage())
                    .type("text/plain")
                    .build();
        };
    }
}
