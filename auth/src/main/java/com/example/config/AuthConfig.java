package com.example.config;

import com.example.models.User;
import com.example.services.UserService;
import com.example.utils.PasswordUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
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

        User user1 = new User("user@gmail.com", passwordUtils.encodePassword("password"));
        user1.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        userList.add(user1);

        User user2 = new User("user2@gmail.com", passwordUtils.encodePassword("password2"));
        user2.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        userList.add(user2);

        userList.forEach(userService::addUserToDatabase);
    }

}
