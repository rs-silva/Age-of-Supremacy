package com.example.controllers;

import com.example.dto.TokenResponseDTO;
import com.example.models.User;
import com.example.services.AuthService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<TokenResponseDTO> registerUser(@Valid @RequestBody User user) {
        LOG.info("Register user = {}", user.toString());
        TokenResponseDTO response = authService.registerUser(user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("login")
    public ResponseEntity<TokenResponseDTO> loginUser(@Valid @RequestBody User user) {
        LOG.info("Login user = {}", user.toString());
        TokenResponseDTO response = authService.loginUser(user);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateUser/{currentUserEmail}")
    public ResponseEntity<TokenResponseDTO> updateUser(@Valid @RequestBody User user,
                                             @PathVariable String currentUserEmail) {
        LOG.info("Update user = {} with {}", currentUserEmail, user.toString());
        TokenResponseDTO response = authService.updateUser(currentUserEmail, user);

        return ResponseEntity.ok(response);
    }

}

