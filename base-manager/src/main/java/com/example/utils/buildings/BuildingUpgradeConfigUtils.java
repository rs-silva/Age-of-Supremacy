package com.example.utils.buildings;

import com.example.config.BuildingConfig;
import com.example.config.BuildingLevelConfig;
import com.example.config.BuildingUpgradeConfig;
import com.example.enums.ResourceNames;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BuildingUpgradeConfigUtils {

    private final BuildingConfig buildingConfig;

    public BuildingUpgradeConfigUtils(BuildingConfig buildingConfig) {
        this.buildingConfig = buildingConfig;
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
                    .filter(levelConfig -> levelConfig.getLevel() == buildingLevel)
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
                        .ifPresent(buildingResourceConfig -> resources.put(buildingResourceConfig.getResourceName(), buildingResourceConfig.getQuantity()));
            }

            return resources;
        }

        return null;
    }

}
