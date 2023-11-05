package com.example.controllers;

import com.example.dto.LoginResponseDTO;
import com.example.models.User;
import com.example.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("{userId}")
    public ResponseEntity<LoginResponseDTO> updateUser(@Valid @RequestBody User updatedUser,
                                                       @PathVariable UUID userId,
                                                       @RequestParam String currentUserEmail) {
        LOG.info("Update user = {} with {}", currentUserEmail, updatedUser.toString());
        LoginResponseDTO response = userService.updateUser(userId, currentUserEmail, updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId,
                                             @RequestParam String userEmail) {
        LOG.info("Delete user with id {}", userId);
        userService.deleteUser(userId, userEmail);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("findAll")
    public ResponseEntity<List<User>> findAll() {
        List<User> usersList = userService.findAllUsers();

        return ResponseEntity.ok(usersList);
    }

}

