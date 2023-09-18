package com.example.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordUtils(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean validateLoginCredentials(String loginPassword, String databasePassword) {
        return passwordEncoder.matches(loginPassword, databasePassword);
    }
}
