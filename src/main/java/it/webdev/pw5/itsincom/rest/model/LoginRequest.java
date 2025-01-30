package it.webdev.pw5.itsincom.rest.model;

import jakarta.validation.constraints.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class LoginRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Email cannot contain spaces")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Password cannot contain spaces")
    private String password;

    public LoginRequest () {

    }

    public void sanitize() {
        this.email = sanitizeInput(this.email);
        this.password = sanitizeInput(this.password);
    }

    // Utilize JSoup to sanitize and remove dangerous input
    private String sanitizeInput(String input) {
        if (input != null) {
            return Jsoup.clean(input, Safelist.basic());
        }
        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
