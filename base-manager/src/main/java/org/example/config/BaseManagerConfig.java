package org.example.config;

import org.example.enums.ResourceNames;
import org.example.models.Base;
import org.example.models.Building;
import org.example.models.Player;
import org.example.repositories.BaseRepository;
import org.example.repositories.BuildingRepository;
import org.example.repositories.PlayerRepository;
import org.example.utils.BaseUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
public class BaseManagerConfig {

    private final PlayerRepository playerRepository;

    private final BaseRepository baseRepository;

    private final BuildingRepository buildingRepository;

    public BaseManagerConfig(PlayerRepository playerRepository, BaseRepository baseRepository, BuildingRepository buildingRepository) {
        this.playerRepository = playerRepository;
        this.baseRepository = baseRepository;
        this.buildingRepository = buildingRepository;
    }

    @Profile("test")
    @Bean
    public void populateDB() {
        Player player = new Player();
        player.setId(UUID.randomUUID());
        player.setUsername("Player");
        player.setTotalScore(0);
        playerRepository.save(player);

        Map<String, Integer> resources = new HashMap<>();
        resources.put(ResourceNames.RESOURCE_1.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_2.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_3.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_4.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_5.getLabel(), 1000);

        Base base = Base.builder()
                .name("Player1_base")
                .x_coordinate("123")
                .y_coordinate("123")
                .player(player)
                .score(0)
                .resources(resources)
                .build();
        baseRepository.save(base);

        List<Building> buildings = BaseUtils.generateBuildingListForNewBase();
        for (Building building : buildings) {
            building.setBase(base);
            buildingRepository.save(building);
        }
    }

}
