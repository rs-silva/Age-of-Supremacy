package com.example.controllers;

import com.example.dto.UserLoginDTO;
import com.example.models.User;
import com.example.services.AuthService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        LOG.info("Register user = {}", user.toString());
        user.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        authService.registerUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        LOG.info("Login user = {}", userLoginDTO.toString());
        String token = authService.loginUser(userLoginDTO);
        return ResponseEntity.ok(token);
    }

}

