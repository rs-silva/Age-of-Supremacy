package com.example.config;

import com.example.models.User;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthConfig {

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthConfig(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Profile("test")
    @Bean
    public void populateDB() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "user", passwordEncoder.encode("password")));
        userList.add(new User(2L, "user2", passwordEncoder.encode("password2")));

        userList.forEach(userService::addUserToDatabase);
    }

}
