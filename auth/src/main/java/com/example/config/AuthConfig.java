package com.example.config;

import com.example.models.User;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthConfig {

    private final UserService userService;

    @Autowired
    public AuthConfig(UserService userService) {
        this.userService = userService;
    }

    @Profile("test")
    @Bean
    public void populateDB() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "user", "password"));
        userList.add(new User(2L, "user2", "password2"));

        userList.forEach(userService::addUserToDatabase);
    }

}
