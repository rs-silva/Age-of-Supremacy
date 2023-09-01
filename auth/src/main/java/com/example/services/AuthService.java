package com.example.services;

import com.example.dto.UserDTO;
import com.example.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
        LOG.info("user = {}", user.toString());
        String url = "http://localhost:8082/api/users";
        String jsonString = JsonUtils.asJsonString(user);
        LOG.info("user jsonString = {}", jsonString);
        LOG.info("url = {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a HttpEntity with headers and request body
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonString, headers);

        restTemplate.postForEntity(url, httpEntity, String.class);
    }

    public void loginUser(UserDTO user) {
        String url = "http://localhost:8082/api/users/" + user.getUsername();
        restTemplate.getForEntity(url, String.class);
    }

}
