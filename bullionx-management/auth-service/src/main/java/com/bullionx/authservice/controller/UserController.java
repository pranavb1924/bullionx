package com.bullionx.authservice.controller;

import com.bullionx.authservice.dto.UserRequestDTO;
import com.bullionx.authservice.dto.UserResponseDTO;
import com.bullionx.authservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userResponseDTOS = this.userService.getUsers();
        return ResponseEntity.ok(userResponseDTOS);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(this.userService.createUser(userRequestDTO));
    }
}