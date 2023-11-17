package org.example.utils;

import org.example.config.BuildingConfig;
import org.example.config.BuildingLevelConfig;
import org.example.config.BuildingUpgradeConfig;
import org.example.dto.BuildingDTO;
import org.example.enums.ResourceNames;
import org.example.mappers.BuildingMapper;
import org.example.models.Base;
import org.example.models.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BuildingUpgradeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingUpgradeUtils.class);

    private final BuildingConfig buildingConfig;

    public BuildingUpgradeUtils(BuildingConfig buildingConfig) {
        this.buildingConfig = buildingConfig;
    }

    public Base upgradeBuilding(Base base, Building building) {
        boolean isBuildingMaxLevel = checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            /* TODO throw exception */
        }

        return null;
    }

    public boolean checkIfBuildingIsMaxLevel(String buildingType, Integer buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = getBuildingUpgradeConfig(buildingType);

        if (buildingUpgradeConfig != null) {
            int buildingMaxLevel = buildingUpgradeConfig.getMaxLevel();

            return buildingLevel >= buildingMaxLevel;
        }

        /* TODO throw exception */
        return true;
    }

    public BuildingDTO getBuildingUpgradeInformation(Building building) {
        Map<String, Integer> resourcesToNextLevel = getAmountOfResourcesToUpgradeBuilding(building.getType(), building.getLevel());
        int constructionTimeToNextLevel = getConstructionTimeToUpgradeBuilding(building.getType(), building.getLevel());

        return BuildingMapper.buildDTO(building, resourcesToNextLevel, constructionTimeToNextLevel);

    }

    public int getConstructionTimeToUpgradeBuilding(String buildingType, int buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = getBuildingUpgradeConfig(buildingType);
        BuildingLevelConfig buildingLevelConfig = getBuildingLevelConfig(buildingUpgradeConfig, buildingLevel);

        if (buildingLevelConfig != null) {
            return buildingLevelConfig.getConstructionTime();
        }

        /* TODO throw exception */
        return 0;
    }

    public Map<String, Integer> getAmountOfResourcesToUpgradeBuilding(String buildingType, int buildingLevel) {
        BuildingUpgradeConfig buildingUpgradeConfig = getBuildingUpgradeConfig(buildingType);
        BuildingLevelConfig buildingLevelConfig = getBuildingLevelConfig(buildingUpgradeConfig, buildingLevel);
        Map<String, Integer> buildingResourceConfig = getBuildingResourceConfig(buildingLevelConfig);

        if (buildingUpgradeConfig != null) {
            return buildingResourceConfig;
        }

        /* TODO Throw exception */
        return null;
    }

    public BuildingUpgradeConfig getBuildingUpgradeConfig(String buildingType) {
        return buildingConfig.getBuildings()
                .stream()
                .filter(building -> building.getBuildingName().equals(buildingType))
                .findFirst()
                .orElse(null);
    }

    public BuildingLevelConfig getBuildingLevelConfig(BuildingUpgradeConfig buildingUpgradeConfig, int buildingLevel) {
        if (buildingUpgradeConfig != null) {
            return buildingUpgradeConfig.getLevels()
                    .stream()
                    .filter(level -> level.getLevel() == buildingLevel + 1)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public Map<String, Integer> getBuildingResourceConfig(BuildingLevelConfig buildingLevelConfig) {
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
