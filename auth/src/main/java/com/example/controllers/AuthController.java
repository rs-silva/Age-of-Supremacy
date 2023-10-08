package com.example.controllers;

import com.example.dto.TokenResponseDTO;
import com.example.dto.UserLoginResponseDTO;
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
    public ResponseEntity<TokenResponseDTO> registerUser(@Valid @RequestBody User user) {
        LOG.info("Register user = {}", user.toString());
        user.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        String token = authService.registerUser(user);
        TokenResponseDTO response = new TokenResponseDTO(user.getEmail(), token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("login")
    public ResponseEntity<TokenResponseDTO> loginUser(@Valid @RequestBody UserLoginResponseDTO userLoginResponseDTO) {
        LOG.info("Login user = {}", userLoginResponseDTO.toString());
        String token = authService.loginUser(userLoginResponseDTO);
        TokenResponseDTO response = new TokenResponseDTO(userLoginResponseDTO.getEmail(), token);

        return ResponseEntity.ok(response);
    }


}

