package com.example.config;

import com.example.interfaces.BuildingInterface;
import com.example.services.buildings.BuildingInterfaceService;
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

    private final List<BuildingInterface> buildingInterfaceList;

    public BaseManagerConfig(List<BuildingInterface> buildingInterfaceList) {
        this.buildingInterfaceList = buildingInterfaceList;
    }

    @Bean
    public BuildingInterfaceService buildingUtilsService() {
        Map<String, BuildingInterface> buildingUtilsMap = new HashMap<>();
        for (BuildingInterface buildingInterface : buildingInterfaceList) {
            String buildingType = buildingInterface.getClass().getAnnotation(Qualifier.class).value();
            buildingUtilsMap.put(buildingType, buildingInterface);
        }

        BuildingInterfaceService buildingInterfaceService = new BuildingInterfaceService();
        buildingInterfaceService.setBuildingInterfacesMap(buildingUtilsMap);

        return buildingInterfaceService;
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
