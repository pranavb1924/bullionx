package com.bullionx.authservice.dto;

public class RegisterResponseDTO {

    private String email;
    private String message;

    public RegisterResponseDTO(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public RegisterResponseDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
