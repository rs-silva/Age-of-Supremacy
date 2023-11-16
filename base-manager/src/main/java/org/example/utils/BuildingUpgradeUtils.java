package org.example.utils;

import org.example.config.BuildingConfig;
import org.example.config.BuildingLevelConfig;
import org.example.config.BuildingResourceConfig;
import org.example.config.BuildingUpgradeConfig;
import org.example.enums.ResourceNames;
import org.example.models.Base;
import org.example.models.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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
        BuildingUpgradeConfig buildingUpgradeConfig = buildingConfig.getBuildings()
                .stream()
                .filter(building -> building.getBuildingName().equals(buildingType))
                .findFirst()
                .orElse(null);

        LOG.info("buildingUpgradeConfig = {}", buildingUpgradeConfig);

        if (buildingUpgradeConfig != null) {
            int buildingMaxLevel = buildingUpgradeConfig.getMaxLevel();

            LOG.info("buildingLevel = {}", buildingLevel);
            LOG.info("buildingMaxLevel = {}", buildingMaxLevel);
            return buildingLevel >= buildingMaxLevel;

        }

        /* TODO throw exception */
        return true;
    }

    public Map<String, Integer> getAmountOfResourcesToUpgradeBuilding(Building building) {
        Map<String, Integer> resources = new HashMap<>();
        Integer resourceAmount;

        for (ResourceNames resourceName : ResourceNames.values()) {
            resourceAmount = getResourceAmount(building.getType(), building.getLevel(), resourceName.getLabel());
            resources.put(resourceName.getLabel(), resourceAmount);
        }

        LOG.info("resources = {}", resources);
        return resources;
    }

    private Integer getResourceAmount(String buildingType, int buildingLevel, String resourceName) {
        BuildingUpgradeConfig buildingUpgradeConfig = buildingConfig.getBuildings()
                .stream()
                .filter(building -> building.getBuildingName().equals(buildingType))
                .findFirst()
                .orElse(null);

        if (buildingUpgradeConfig != null) {
            BuildingLevelConfig buildingLevelConfig = buildingUpgradeConfig.getLevels()
                    .stream()
                    .filter(level -> level.getLevel() == buildingLevel + 1)
                    .findFirst()
                    .orElse(null);

            if (buildingLevelConfig != null) {
                BuildingResourceConfig buildingResourceConfig = buildingLevelConfig.getResources()
                        .stream()
                        .filter(resource -> resource.getResourceName().equals(resourceName))
                        .findFirst()
                        .orElse(null);

                if (buildingResourceConfig != null) {
                    return buildingResourceConfig.getQuantity();
                }
            }
        }

        /* TODO Throw exception */
        return null;
    }
}
