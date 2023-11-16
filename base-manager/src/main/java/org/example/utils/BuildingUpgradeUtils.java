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

        if (buildingUpgradeConfig != null) {
            int buildingMaxLevel = buildingUpgradeConfig.getMaxLevel();

            return buildingLevel >= buildingMaxLevel;
        }

        /* TODO throw exception */
        return true;
    }

    public Map<String, Integer> getResourceAmount(String buildingType, int buildingLevel) {
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
        }

        /* TODO Throw exception */
        return null;
    }
}
