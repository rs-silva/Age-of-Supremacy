package com.example.services;

import com.example.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Autowired
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void registerUser(UserDTO user) {
        String url = "http://localhost:8082/api/users/" + user.getUsername();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    }

    public void loginUser(UserDTO user) {
        String url = "http://localhost:8082/api/users/" + user.getUsername();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    }

}
