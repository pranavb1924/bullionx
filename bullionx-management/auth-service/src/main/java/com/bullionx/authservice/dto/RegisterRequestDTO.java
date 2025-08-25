package com.bullionx.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotBlank(message = "First Name Required")
    private String firstName;

    @NotBlank(message = "First Name Required")
    private String lastName;

    @NotBlank(message = "Email Address Required")
    @Email(message = "Valid Email Address Required")
    private String email;

    @NotBlank(message = "Password Required")
    @Size(min = 8, message = "Password Must Be At least 8 Characters Long")
    private String password;
}
