package org.example.services;

import org.example.dto.BuildingDTO;
import org.example.exceptions.ResourceNotFoundException;
import org.example.models.Base;
import org.example.models.Building;
import org.example.repositories.BuildingRepository;
import org.example.utils.BuildingGenerationUtils;
import org.example.utils.BuildingUpgradeUtils;
import org.example.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final BuildingGenerationUtils buildingGenerationUtils;

    private final RestTemplate restTemplate;

    public BuildingService(BuildingRepository buildingRepository, BuildingUpgradeUtils buildingUpgradeUtils, BuildingGenerationUtils buildingGenerationUtils, RestTemplate restTemplate) {
        this.buildingRepository = buildingRepository;
        this.buildingUpgradeUtils = buildingUpgradeUtils;
        this.buildingGenerationUtils = buildingGenerationUtils;
        this.restTemplate = restTemplate;
    }

    public void generateDefaultBuildingsForNewBase(Base base) {
        List<Building> buildingList = buildingGenerationUtils.generateDefaultBuildingsForNewBase();

        for (Building building : buildingList) {
            building.setBase(base);
            buildingRepository.save(building);
        }

    }

    public BuildingDTO getBuildingInformation(Building building) {
        boolean isBuildingMaxLevel = buildingUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            return null;
        }

        return buildingUpgradeUtils.getBuildingUpgradeInformation(building);
    }

    public void upgradeBuilding(UUID buildingId) {
        /* TODO Validate if the building belongs to the player */
        Building building = findById(buildingId);
        Base base = building.getBase();
        buildingUpgradeUtils.upgradeBuilding(base, building);


        /* TODO Remove hardcoded url */
        String url = "http://localhost:8083/api/event";
        //restTemplate.postForObject(url, new BuildingUpgradeEventDTO("b"), BuildingUpgradeEventDTO.class);

    }

    public Building findById(UUID id) {
        Optional<Building> building = buildingRepository.findById(id);

        if (building.isEmpty()) {
            throw new ResourceNotFoundException(String.format(
                    Constants.BUILDING_NOT_FOUND, id));
        }

        return building.get();
    }

}
