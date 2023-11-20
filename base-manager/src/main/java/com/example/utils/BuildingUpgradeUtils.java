package com.example.utils;

import com.example.config.BuildingLevelConfig;
import com.example.config.BuildingUpgradeConfig;
import com.example.dto.BuildingUpgradeEventDTO;
import com.example.enums.BuildingsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.config.BuildingConfig;
import com.example.enums.ResourceNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class BuildingUpgradeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingUpgradeUtils.class);

    private final BuildingConfig buildingConfig;

    private final RestTemplate restTemplate;

    public BuildingUpgradeUtils(BuildingConfig buildingConfig, RestTemplate restTemplate) {
        this.buildingConfig = buildingConfig;
        this.restTemplate = restTemplate;
    }

    public void upgradeBuilding(Base base, Building building) {
        Map<String, Double> baseResources = base.getResources();
        Map<String, Integer> resourcesRequired = getRequirementsToUpgradeBuilding(building.getType(), building.getLevel());

        /* Remove time requirement which is not needed for this (only resources) */
        Integer constructionTime = resourcesRequired.remove(BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel());

        for (String resourceName : resourcesRequired.keySet()) {
            Double currentResourceAmount = baseResources.get(resourceName);
            Integer resourceAmountRequired = resourcesRequired.get(resourceName);

            baseResources.put(resourceName, currentResourceAmount - resourceAmountRequired);
        }

        Timestamp endTime = Timestamp.from(Instant.now().plusMillis(constructionTime * 1000));

        BuildingUpgradeEventDTO buildingUpgradeEventDTO = BuildingUpgradeEventDTO.builder()
                .buildingId(building.getId())
                .completionTime(endTime)
                .build();

        /* TODO Remove hardcoded url */
        String url = "http://localhost:8083/api/event";
        restTemplate.postForObject(url, buildingUpgradeEventDTO, BuildingUpgradeEventDTO.class);

    }

    public boolean checkIfThereAreEnoughResourcesToUpgradeBuilding(Base base, Building building) {
        Map<String, Double> baseResources = base.getResources();
        Map<String, Integer> resourcesRequired = getRequirementsToUpgradeBuilding(building.getType(), building.getLevel());

        /* Remove time requirement which is not needed for this (only resources) */
        resourcesRequired.remove(BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel());

        for (String resourceName : resourcesRequired.keySet()) {
            if (baseResources.containsKey(resourceName)) {
                Double currentResourceAmount = baseResources.get(resourceName);
                Integer resourceAmountRequired = resourcesRequired.get(resourceName);
                if (currentResourceAmount < resourceAmountRequired) {
                    return false;
                }
            }
            else {
                LOG.info("There was an error while upgrading building {}\n" +
                        "The base does not contain information about resource {}", building.getId(), resourceName);
                throw new InternalServerErrorException(Constants.BASE_NO_INFORMATION_ABOUT_RESOURCE_AMOUNT);
            }
        }

        return true;
    }

    public boolean checkIfBuildingIsMaxLevel(String buildingType, Integer buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = getBuildingUpgradeConfig(buildingType);

        if (buildingUpgradeConfig != null) {
            int buildingMaxLevel = buildingUpgradeConfig.getMaxLevel();

            return buildingLevel >= buildingMaxLevel;
        }

        LOG.info("There was an error while retrieving the upgrade information for building {} for level {}", buildingType, buildingLevel + 1);
        throw new InternalServerErrorException(Constants.BUILDING_UPGRADE_NOT_FOUND_ERROR);
    }

    public Map<String, Integer> getRequirementsToUpgradeBuilding(String buildingType, int buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = getBuildingUpgradeConfig(buildingType);
        BuildingLevelConfig buildingLevelConfig = getBuildingLevelConfig(buildingUpgradeConfig, buildingLevel + 1);
        Map<String, Integer> buildingResourceConfig = getBuildingResourceConfig(buildingLevelConfig);

        if (buildingUpgradeConfig != null) {
            buildingResourceConfig.put(
                    BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel(), buildingLevelConfig.getConstructionTime());
            return buildingResourceConfig;
        }

        LOG.info("There was an error while retrieving the upgrade information for building {} for level {}", buildingType, buildingLevel);
        throw new InternalServerErrorException(Constants.BUILDING_UPGRADE_NOT_FOUND_ERROR);
    }

    private BuildingUpgradeConfig getBuildingUpgradeConfig(String buildingType) {
        return buildingConfig.getBuildings()
                .stream()
                .filter(building -> building.getBuildingName().equals(buildingType))
                .findFirst()
                .orElse(null);
    }

    private BuildingLevelConfig getBuildingLevelConfig(BuildingUpgradeConfig buildingUpgradeConfig, int buildingLevel) {
        if (buildingUpgradeConfig != null) {
            return buildingUpgradeConfig.getLevels()
                    .stream()
                    .filter(level -> level.getLevel() == buildingLevel)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private Map<String, Integer> getBuildingResourceConfig(BuildingLevelConfig buildingLevelConfig) {
        if (buildingLevelConfig != null) {
            Map<String, Integer> resources = new HashMap<>();
            for (ResourceNames resourceName : ResourceNames.values()) {
                buildingLevelConfig.getResources()
                        .stream()
                        .filter(resource -> resource.getResourceName().equals(resourceName.getLabel()))
                        .findFirst()
                        .ifPresent(buildingResourceConfig -> resources.put(resourceName.getLabel(), buildingResourceConfig.getQuantity()));
            }

            return resources;
        }

        return null;
    }
}
