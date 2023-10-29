package com.example.controllers;

import com.example.dto.TokenResponseDTO;
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

@RestController
@RequestMapping("api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("{userId}")
    public ResponseEntity<TokenResponseDTO> updateUser(@Valid @RequestBody User updatedUser,
                                                       @PathVariable String userId,
                                                       @RequestParam String currentUserEmail) {
        LOG.info("Update user = {} with {}", currentUserEmail, updatedUser.toString());
        TokenResponseDTO response = userService.updateUser(userId, currentUserEmail, updatedUser);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId,
                                             @RequestParam String currentUserEmail) {
        LOG.info("Delete user with id {}", userId);
        userService.deleteUser(userId, currentUserEmail);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("findAll")
    public ResponseEntity<List<User>> findAll() {
        List<User> usersList = userService.findAllUsers();

        return ResponseEntity.ok(usersList);
    }

}
