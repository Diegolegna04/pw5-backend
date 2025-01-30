package it.webdev.pw5.itsincom.rest.model;

import it.webdev.pw5.itsincom.service.exception.XSSAttackAttempt;
import jakarta.validation.constraints.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class RegisterRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Email cannot contain spaces")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Password cannot contain spaces")
    private String password;

    public RegisterRequest() {

    }

    public void sanitize() throws XSSAttackAttempt {
        this.name = sanitizeInput(this.name);
        this.email = sanitizeInput(this.email);
        this.password = sanitizeInput(this.password);
    }

    // Utilize JSoup to sanitize and remove dangerous input
    private String sanitizeInput(String input) throws XSSAttackAttempt {
        // Sanitize input
        String sanitizedInput = Jsoup.clean(input, Safelist.basic());
        if (sanitizedInput.isEmpty()) {
            throw new XSSAttackAttempt();
        }
        return sanitizedInput;
    }

    public @NotBlank(message = "Name cannot be empty") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name cannot be empty") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Email cannot be empty") @Email(message = "Please provide a valid email address") @Pattern(regexp = "^(?!.*\\s).*$", message = "Email cannot contain spaces") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email cannot be empty") @Email(message = "Please provide a valid email address") @Pattern(regexp = "^(?!.*\\s).*$", message = "Email cannot contain spaces") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password cannot be empty") @Size(min = 6, message = "Password must be at least 6 characters long") @Pattern(regexp = "^(?!.*\\s).*$", message = "Password cannot contain spaces") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password cannot be empty") @Size(min = 6, message = "Password must be at least 6 characters long") @Pattern(regexp = "^(?!.*\\s).*$", message = "Password cannot contain spaces") String password) {
        this.password = password;
    }
}
