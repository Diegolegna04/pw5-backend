package it.webdev.pw5.itsincom.rest.model;

import jakarta.validation.constraints.*;

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
