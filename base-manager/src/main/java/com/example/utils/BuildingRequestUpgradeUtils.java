package com.example.utils;

import com.example.config.BuildingLevelConfig;
import com.example.config.BuildingUpgradeConfig;
import com.example.dto.BuildingUpgradeEventDTO;
import com.example.enums.BuildingsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.models.Base;
import com.example.models.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Component
public class BuildingRequestUpgradeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingRequestUpgradeUtils.class);

    private final BuildingUpgradeConfigUtils buildingUpgradeConfigUtils;

    private final RestTemplate restTemplate;

    public BuildingRequestUpgradeUtils(BuildingUpgradeConfigUtils buildingUpgradeConfigUtils, RestTemplate restTemplate) {
        this.buildingUpgradeConfigUtils = buildingUpgradeConfigUtils;
        this.restTemplate = restTemplate;
    }

    public void requestBuildingUpgrade(Base base, Building building) {
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
        /* Send Building Upgrade Event to event-manager module */
        String url = "http://localhost:8083/api/event/building/upgrade";
        restTemplate.postForObject(url, buildingUpgradeEventDTO, BuildingUpgradeEventDTO.class);
    }

    public boolean checkIfThereAreEnoughResourcesToUpgradeBuilding(Base base, String buildingType, int buildingLevel) {
        Map<String, Double> baseResources = base.getResources();
        Map<String, Integer> resourcesRequired = getRequirementsToUpgradeBuilding(buildingType, buildingLevel);

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
                LOG.info("There was an error while upgrading a building.\n" +
                        "The base {} does not contain information about resource {}", base.getId(), resourceName);
                throw new InternalServerErrorException(Constants.BASE_NO_INFORMATION_ABOUT_RESOURCE_AMOUNT);
            }
        }

        return true;
    }

    public boolean checkIfBuildingIsMaxLevel(String buildingType, Integer buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = buildingUpgradeConfigUtils.getBuildingUpgradeConfig(buildingType);

        if (buildingUpgradeConfig != null) {
            int buildingMaxLevel = buildingUpgradeConfig.getMaxLevel();

            return buildingLevel >= buildingMaxLevel;
        }

        LOG.info("There was an error while retrieving the upgrade information for building {} for level {}", buildingType, buildingLevel + 1);
        throw new InternalServerErrorException(Constants.BUILDING_UPGRADE_NOT_FOUND_ERROR);
    }

    public Map<String, Integer> getRequirementsToUpgradeBuilding(String buildingType, int buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = buildingUpgradeConfigUtils.getBuildingUpgradeConfig(buildingType);
        BuildingLevelConfig buildingLevelConfig = buildingUpgradeConfigUtils.getBuildingLevelConfig(buildingUpgradeConfig, buildingLevel + 1);
        Map<String, Integer> buildingResourceConfig = buildingUpgradeConfigUtils.getBuildingResourceConfig(buildingLevelConfig);

        if (buildingUpgradeConfig != null) {
            buildingResourceConfig.put(
                    BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel(), buildingLevelConfig.getConstructionTime());
            return buildingResourceConfig;
        }

        LOG.info("There was an error while retrieving the upgrade information for {} (lv.{})", buildingType, buildingLevel);
        throw new InternalServerErrorException(Constants.BUILDING_UPGRADE_NOT_FOUND_ERROR);
    }

    public int getBuildingScoreForSpecificLevel(String buildingType, int buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = buildingUpgradeConfigUtils.getBuildingUpgradeConfig(buildingType);
        BuildingLevelConfig buildingLevelConfig = buildingUpgradeConfigUtils.getBuildingLevelConfig(buildingUpgradeConfig, buildingLevel);

        if (buildingUpgradeConfig != null) {
            return buildingLevelConfig.getScore();
        }

        LOG.info("There was an error while retrieving the upgrade information for {} (lv.{})", buildingType, buildingLevel);
        throw new InternalServerErrorException(Constants.BUILDING_UPGRADE_NOT_FOUND_ERROR);
    }


}
