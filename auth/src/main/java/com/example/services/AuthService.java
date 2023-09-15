package com.example.services;

import com.example.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    public void registerUser(UserDTO user) {
        LOG.info("user = {}", user.toString());

    }

    public void loginUser(UserDTO user) {
    }

}
