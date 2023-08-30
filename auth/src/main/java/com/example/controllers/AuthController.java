package com.example.controllers;

import com.example.models.User;
import com.example.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<String> loginUser() {
        authService.loginUser(new User());
        return ResponseEntity.ok().build();
    }

    @PostMapping("register")
    public ResponseEntity<String> registerUser() {
        return ResponseEntity.ok().build();
    }
}

