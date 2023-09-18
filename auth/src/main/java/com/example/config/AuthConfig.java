package com.example.config;

import com.example.models.User;
import com.example.services.UserService;
import com.example.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthConfig {

    private final UserService userService;

    private final PasswordUtils passwordUtils;

    public AuthConfig(UserService userService, PasswordUtils passwordUtils) {
        this.userService = userService;
        this.passwordUtils = passwordUtils;
    }

    @Profile("test")
    @Bean
    public void populateDB() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "user@gmail.com", passwordUtils.encodePassword("password"), List.of("USER")));
        userList.add(new User(2L, "user2@gmail.com", passwordUtils.encodePassword("password2"), List.of("USER")));

       userList.forEach(userService::addUserToDatabase);
    }

}
