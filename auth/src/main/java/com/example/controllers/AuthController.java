package com.example.controllers;

import com.example.dto.TokenResponseDTO;
import com.example.models.User;
import com.example.services.AuthService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/user/{userId}")
    public ResponseEntity<TokenResponseDTO> updateUser(@Valid @RequestBody User updatedUser,
                                                       @PathVariable String userId,
                                                       @RequestParam String currentUserEmail) {
        LOG.info("Update user = {} with {}", currentUserEmail, updatedUser.toString());
        TokenResponseDTO response = authService.updateUser(userId, currentUserEmail, updatedUser);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId,
                                             @RequestParam String currentUserEmail) {
        LOG.info("Delete user with id {}", userId);
        authService.deleteUser(userId, currentUserEmail);

        return ResponseEntity.ok().build();
    }

}

