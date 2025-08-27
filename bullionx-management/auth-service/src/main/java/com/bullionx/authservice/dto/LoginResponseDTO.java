package com.bullionx.authservice.dto;

import java.util.UUID;

public class LoginResponseDTO {

    private final String token;
    private String email;
    private String firstName;
    private String lastName;
    private String userId;

    public LoginResponseDTO(String userId, String token, String email, String firstName, String lastName) {
        this.userId = userId;
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
