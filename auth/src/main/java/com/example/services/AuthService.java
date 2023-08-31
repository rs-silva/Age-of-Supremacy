package com.example.services;

import com.example.controllers.AuthController;
import com.example.dto.UserDTO;
import com.example.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void registerUser(UserDTO user) {
        LOG.error("user = {}", user.toString());
        String url = "http://localhost:8082/api/users" + user.getUsername();
        String jsonString = JsonUtils.asJsonString(user);
        LOG.error("user jsonString = {}", jsonString);
        ResponseEntity<String> response = restTemplate.postForEntity(url, jsonString, String.class);

    }

    public void loginUser(UserDTO user) {
        String url = "http://localhost:8082/api/users/" + user.getUsername();
        LOG.error("user = {}", user);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    }

}
