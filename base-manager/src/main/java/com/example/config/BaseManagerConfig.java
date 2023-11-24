package com.example.config;

import com.example.interfaces.BuildingUtils;
import com.example.services.buildings.BuildingUtilsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BaseManagerConfig {

    private final List<BuildingUtils> buildingUtilsList;

    public BaseManagerConfig(List<BuildingUtils> buildingUtilsList) {
        this.buildingUtilsList = buildingUtilsList;
    }

    @Bean
    public BuildingUtilsService buildingUtilsService() {
        Map<String, BuildingUtils> buildingUtilsMap = new HashMap<>();
        for (BuildingUtils buildingUtils : buildingUtilsList) {
            String buildingType = buildingUtils.getClass().getAnnotation(Qualifier.class).value();
            buildingUtilsMap.put(buildingType, buildingUtils);
        }

        BuildingUtilsService buildingUtilsService = new BuildingUtilsService();
        buildingUtilsService.setBuildingUtilsMap(buildingUtilsMap);

        return buildingUtilsService;
    }

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
