package com.example.config;

import com.example.models.BuildingUpgradeEvent;
import com.example.repositories.BuildingUpgradeEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class EventManagerConfig {

    private final BuildingUpgradeEventRepository buildingUpgradeEventRepository;

    public EventManagerConfig(BuildingUpgradeEventRepository buildingUpgradeEventRepository) {
        this.buildingUpgradeEventRepository = buildingUpgradeEventRepository;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*@Bean
    public void populateDB() {

        for (int i = 0; i < 100000; i++) {
            BuildingUpgradeEvent buildingUpgradeEvent = BuildingUpgradeEvent.builder()
                    .buildingId(UUID.randomUUID())
                    .completionTime(Timestamp.from(Instant.now().plusMillis(50000000)))
                    .build();
            buildingUpgradeEventRepository.save(buildingUpgradeEvent);
        }

    }*/

}
