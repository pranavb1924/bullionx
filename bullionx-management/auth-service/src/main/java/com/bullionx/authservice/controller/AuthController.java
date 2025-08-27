package com.bullionx.authservice.controller;

import com.bullionx.authservice.dto.LoginRequestDTO;
import com.bullionx.authservice.dto.LoginResponseDTO;
import com.bullionx.authservice.dto.RegisterRequestDTO;
import com.bullionx.authservice.dto.RegisterResponseDTO;
import com.bullionx.authservice.model.User;
import com.bullionx.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        Optional<String> optionalToken = authService.authenticate(loginRequestDTO);

        if (optionalToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = optionalToken.get();
        Optional<User> user = authService.getUser(loginRequestDTO.getEmail());


        return ResponseEntity.ok(new LoginResponseDTO(user.get().getId().toString(), token, user.get().getEmail(), user.get().getFirstName(), user.get().getLastName()));

    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        Optional<User> user = this.authService.getUser(registerRequestDTO.getEmail());
        RegisterResponseDTO responseDTO = new RegisterResponseDTO();
        if (user.isEmpty()){
            responseDTO = authService.createUser(registerRequestDTO);
        }

        return ResponseEntity.ok(responseDTO);

    }
}
