package com.example.controllers;

import com.example.dto.UserLoginDTO;
import com.example.models.User;
import com.example.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        LOG.info("Register user = {}", user.toString());
        authService.registerUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        LOG.info("Login user = {}", userLoginDTO.toString());
        String token = authService.loginUser(userLoginDTO);
        return ResponseEntity.ok(token);
    }

}

