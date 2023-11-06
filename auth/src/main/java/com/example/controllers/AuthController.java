package com.example.controllers;

import com.example.dto.LoginRequestDTO;
import com.example.dto.LoginResponseDTO;
import com.example.dto.RefreshTokenResponseDTO;
import com.example.models.User;
import com.example.services.AuthService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register")
    public ResponseEntity<LoginResponseDTO> registerUser(@Valid @RequestBody User user) {
        LOG.info("Register user = {}", user.toString());
        LoginResponseDTO response = authService.registerUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO user) {
        LOG.info("Login user = {}", user.toString());
        LoginResponseDTO response = authService.loginUser(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("refreshToken")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestParam String userEmail,
                                                                @RequestParam String refreshToken) {
        LOG.info("Refreshing token {} from user {}", refreshToken, userEmail);
        RefreshTokenResponseDTO response = authService.refreshToken(userEmail, refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("logout")
    public ResponseEntity<RefreshTokenResponseDTO> logout(@RequestParam String userEmail) {
        LOG.info("Logging out user {}", userEmail);
        authService.logout(userEmail);

        return ResponseEntity.ok().build();
    }

}

