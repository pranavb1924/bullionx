package com.bullionx.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.aspectj.bridge.IMessage;

@Getter
@Setter
public class UserResponseDTO {

    @NotBlank(message = "Name is Required")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String firstName;
    @NotBlank(message = "Name is Required")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
