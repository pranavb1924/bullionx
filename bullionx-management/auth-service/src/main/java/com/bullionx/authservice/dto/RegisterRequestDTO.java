package com.bullionx.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

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
