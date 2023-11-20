package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BaseManagerConfig {

    @Profile("test")
    @Bean
    public void populateDB() {
        //NewPlayerDTO newPlayerDTO = new NewPlayerDTO(UUID.randomUUID().toString(), "Player");
        //playerService.createPlayer(newPlayerDTO);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
