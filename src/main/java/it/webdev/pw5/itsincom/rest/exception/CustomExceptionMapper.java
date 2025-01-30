package it.webdev.pw5.itsincom.rest.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getClass().getSimpleName());
        errorResponse.put("message", getErrorMessage(e));

        return Response.status(getStatusCode(e))
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getErrorMessage(Exception e) {
        return switch (e.getClass().getSimpleName()) {
            case "EmailNotAvailable" -> "This email is already in use.";
            case "EmptyField" -> "Please fill in all required fields.";
            case "WrongEmailOrPassword" -> "Email or password are incorrect.";
            case "LoginNotPossible" -> "A session is already active for this user. Please log out before trying again.";
            case "SessionNotFound" -> "Session not found.";
            case "InvalidEmailFormat" -> "Email format is invalid.";
            case "CookieIsNull" -> "Cookie is null.";
            case "UserNotFound" -> "User not found.";
            case "UserUnauthorized" -> "User is not authorized to perform this action.";
            default -> "Unexpected error: " + e.getMessage();
        };
    }

    private Response.Status getStatusCode(Exception e) {
        return switch (e.getClass().getSimpleName()) {
            case "EmailNotAvailable", "LoginNotPossible" -> Response.Status.CONFLICT;
            case "EmptyField", "InvalidEmailFormat", "CookieIsNull" -> Response.Status.BAD_REQUEST;
            case "WrongEmailOrPassword", "UserUnauthorized" -> Response.Status.UNAUTHORIZED;
            case "SessionNotFound", "UserNotFound" -> Response.Status.NOT_FOUND;
            default -> Response.Status.INTERNAL_SERVER_ERROR;
        };
    }
}
