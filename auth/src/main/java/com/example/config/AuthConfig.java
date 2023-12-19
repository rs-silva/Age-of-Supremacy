package com.example.config;

import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.PasswordUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AuthConfig {

    private final UserRepository userRepository;

    private final PasswordUtils passwordUtils;

    public AuthConfig(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    /*@Profile("test")
    @Bean
    public void populateDB() {
        List<User> userList = new ArrayList<>();

        User user1 = new User("admin@mail.com", "admin", passwordUtils.encodePassword("123"));
        user1.addRole(new SimpleGrantedAuthority("ROLE_ADMIN"));
        userList.add(user1);

        User user2 = new User("user@mail.com", "user", passwordUtils.encodePassword("123"));
        user2.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        userList.add(user2);

        userRepository.saveAll(userList);
    }*/

}
