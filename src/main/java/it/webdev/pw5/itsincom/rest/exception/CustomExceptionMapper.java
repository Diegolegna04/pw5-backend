package it.webdev.pw5.itsincom.rest.exception;

import it.webdev.pw5.itsincom.service.exception.EmailNotAvailable;
import it.webdev.pw5.itsincom.service.exception.EmptyField;
import it.webdev.pw5.itsincom.service.exception.LoginNotPossible;
import it.webdev.pw5.itsincom.service.exception.WrongEmailOrPassword;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof EmailNotAvailable) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Questa email è già collegata ad un altro account")
                    .type("text/plain")
                    .build();
        } else if (e instanceof EmptyField) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Tutti i campi devono essere compilati")
                    .type("text/plain")
                    .build();
        } else if (e instanceof WrongEmailOrPassword) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Username o password errati")
                    .type("text/plain")
                    .build();
        } else if (e instanceof LoginNotPossible) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("È già presente una sessione associata a questo utente: effettuare prima il logout per poter accedere nuovamente")
                    .type("text/plain")
                    .build();
        }


        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Errore imprevisto: " + e.getMessage())
                .type("text/plain")
                .build();
    }
}
