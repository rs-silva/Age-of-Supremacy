package org.example.config;

import org.example.dto.NewPlayerDTO;
import org.example.services.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Configuration
public class BaseManagerConfig {

    private final PlayerService playerService;

    public BaseManagerConfig(PlayerService playerService) {
        this.playerService = playerService;
    }


    @Profile("test")
    @Bean
    public void populateDB() {
        //NewPlayerDTO newPlayerDTO = new NewPlayerDTO(UUID.randomUUID().toString(), "Player");
        //playerService.createPlayer(newPlayerDTO);
    }

}
