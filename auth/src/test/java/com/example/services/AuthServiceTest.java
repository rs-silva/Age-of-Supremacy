package com.example.services;

import com.example.AuthApplication;
import com.example.models.User;
import com.example.utils.JwtTokenUtils;
import com.example.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private UserService userService;

    @Test
    void registerUserTest() {
        User user = getTestUser();
        authService.registerUser(user);
    }

    private User getTestUser() {
        return new User("test@mail.com", "123");
    }

}