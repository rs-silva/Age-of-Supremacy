package org.example.utils;

import org.example.config.BuildingConfig;
import org.example.config.BuildingLevelConfig;
import org.example.config.BuildingResourceConfig;
import org.example.config.BuildingUpgradeConfig;
import org.example.enums.ResourceNames;
import org.example.models.Base;
import org.example.models.Building;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BuildingUpgradeUtils {

    private final BuildingConfig buildingConfig;

    public BuildingUpgradeUtils(BuildingConfig buildingConfig) {
        this.buildingConfig = buildingConfig;
    }

    public Base upgradeBuilding(Base base, Building building) {

    }

    public Map<String, Integer> getAmountOfResourcesToUpgradeBuilding(Building building) {
        Map<String, Integer> resources = new HashMap<>();

        Integer resourceAmount = getResourceAmount(building.getType(), building.getLevel(), ResourceNames.RESOURCE_1.getLabel());
        resources.put(ResourceNames.RESOURCE_1.getLabel(), resourceAmount);

        resourceAmount = getResourceAmount(building.getType(), building.getLevel(), ResourceNames.RESOURCE_2.getLabel());
        resources.put(ResourceNames.RESOURCE_2.getLabel(), resourceAmount);

        resourceAmount = getResourceAmount(building.getType(), building.getLevel(), ResourceNames.RESOURCE_3.getLabel());
        resources.put(ResourceNames.RESOURCE_3.getLabel(), resourceAmount);

        resourceAmount = getResourceAmount(building.getType(), building.getLevel(), ResourceNames.RESOURCE_4.getLabel());
        resources.put(ResourceNames.RESOURCE_4.getLabel(), resourceAmount);

        resourceAmount = getResourceAmount(building.getType(), building.getLevel(), ResourceNames.RESOURCE_5.getLabel());
        resources.put(ResourceNames.RESOURCE_5.getLabel(), resourceAmount);

        return resources;
    }

    private Integer getResourceAmount(String buildingType, int buildingLevel, String resourceName) {
        BuildingUpgradeConfig buildingUpgradeConfig = buildingConfig.getBuildings()
                .stream()
                .filter(building -> building.getBuildingName().equals(buildingType))
                .findFirst()
                .orElse(null);

        /* TODO Check if the building is already at the maximum level */

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
