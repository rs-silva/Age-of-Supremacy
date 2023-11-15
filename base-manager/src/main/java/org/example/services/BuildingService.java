package org.example.services;

import org.example.dto.EventDTO;
import org.example.models.Base;
import org.example.models.Building;
import org.example.repositories.BuildingRepository;
import org.example.utils.BuildingGenerationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    private final RestTemplate restTemplate;

    public BuildingService(BuildingRepository buildingRepository, RestTemplate restTemplate) {
        this.buildingRepository = buildingRepository;
        this.restTemplate = restTemplate;
    }

    public void generateDefaultBuildingsForNewBase(Base base) {
        List<Building> buildingList = BuildingGenerationUtils.generateDefaultBuildingsForNewBase();

        for (Building building : buildingList) {
            building.setBase(base);
            buildingRepository.save(building);
        }

    }

    public void upgradeBuilding() {
        String url = "http://localhost:8083/api/event";
        restTemplate.postForObject(url, new EventDTO("b"), EventDTO.class);

    }

}
