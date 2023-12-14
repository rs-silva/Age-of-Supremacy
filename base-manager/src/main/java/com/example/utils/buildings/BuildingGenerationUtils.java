package com.example.utils.buildings;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.enums.BuildingNames;
import com.example.enums.BuildingsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.services.buildings.BuildingUtilsService;
import com.example.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BuildingGenerationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingGenerationUtils.class);

    private final BuildingUtilsService buildingUtilsService;

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final RestTemplate restTemplate;

    public BuildingGenerationUtils(BuildingUtilsService buildingUtilsService, BuildingUpgradeUtils buildingUpgradeUtils, RestTemplate restTemplate) {
        this.buildingUtilsService = buildingUtilsService;
        this.buildingUpgradeUtils = buildingUpgradeUtils;
        this.restTemplate = restTemplate;
    }

    public List<Building> generateDefaultBuildingsForNewBase() {
        List<Building> buildingList = new ArrayList<>();

        /* Resource Production Buildings */
        Building resource1Producer = buildingUtilsService.generateBuilding(BuildingNames.RESOURCE_1_FACTORY.getLabel());
        buildingList.add(resource1Producer);

        Building resource2Producer = buildingUtilsService.generateBuilding(BuildingNames.RESOURCE_2_FACTORY.getLabel());
        buildingList.add(resource2Producer);

        Building resource3Producer = buildingUtilsService.generateBuilding(BuildingNames.RESOURCE_3_FACTORY.getLabel());
        buildingList.add(resource3Producer);

        Building resource4Producer = buildingUtilsService.generateBuilding(BuildingNames.RESOURCE_4_FACTORY.getLabel());
        buildingList.add(resource4Producer);

        Building resource5Producer = buildingUtilsService.generateBuilding(BuildingNames.RESOURCE_5_FACTORY.getLabel());
        buildingList.add(resource5Producer);

        /* Other Buildings */
        Building warehouse = buildingUtilsService.generateBuilding(BuildingNames.WAREHOUSE.getLabel());
        buildingList.add(warehouse);

        Building mainBuilding = buildingUtilsService.generateBuilding(BuildingNames.MAIN_BUILDING.getLabel());
        buildingList.add(mainBuilding);

        return buildingList;
    }

    public boolean checkIfBuildingAlreadyExists(Base base, String buildingType) {
        List<Building> buildingList = base.getBuildings();

        for (Building building : buildingList) {
            if (building.getType().equals(buildingType)) {
                return true;
            }
        }

        return false;
    }

    public void requestBuildingGeneration(Base base, String buildingType) {
        Map<String, Double> baseResources = base.getResources();
        Map<String, Integer> resourcesRequired = getRequirementsToGenerateBuilding(buildingType);

        Integer constructionTime = resourcesRequired.remove(BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel());

        for (String resourceName : resourcesRequired.keySet()) {
            Double currentResourceAmount = baseResources.get(resourceName);
            Integer resourceAmountRequired = resourcesRequired.get(resourceName);

            baseResources.put(resourceName, currentResourceAmount - resourceAmountRequired);
        }

        Timestamp endTime = Timestamp.from(Instant.now().plusMillis(constructionTime * 1000));

        BuildingUpgradeEventDTO buildingUpgradeEventDTO = BuildingUpgradeEventDTO.builder()
                .baseId(base.getId())
                .buildingType(buildingType)
                .completionTime(endTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Building Upgrade Event to event-manager module */
        String url = "http://localhost:8083/api/event/building/upgrade";
        restTemplate.postForObject(url, buildingUpgradeEventDTO, BuildingUpgradeEventDTO.class);
    }

    private Map<String, Integer> getRequirementsToGenerateBuilding(String buildingType) {
        /* TODO Check other possible building requirements to generate a new building
        *   such as level requirements from other buildings in the base */
        return buildingUpgradeUtils.getRequirementsToUpgradeBuilding(buildingType, 0);
    }

    public Building completeBuildingGeneration(Base base, String buildingType) {
        boolean doesBuildingAlreadyExist = checkIfBuildingAlreadyExists(base, buildingType);

        if (doesBuildingAlreadyExist) {
            LOG.info("Attempted to create building {} in base {}, but this building already exists in this base.", buildingType, base.getId());
            throw new InternalServerErrorException(Constants.BUILDING_ALREADY_EXISTS);
        }

        /* TODO Call the generate method for the corresponding building */
        Building newBuilding = buildingUtilsService.generateBuilding(buildingType);

        if (newBuilding == null) {
            LOG.info("Attempted to finish generation of building {} in base {}, but the building's name is invalid.", buildingType, base.getId());
            throw new InternalServerErrorException(Constants.INVALID_BUILDING_NAME);
        }

        return newBuilding;
    }

}
