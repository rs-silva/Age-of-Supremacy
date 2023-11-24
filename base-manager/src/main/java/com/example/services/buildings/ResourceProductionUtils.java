package com.example.services.buildings;

import com.example.enums.BuildingsPropertiesNames;
import com.example.models.Building;
import com.example.utils.BuildingUpgradeUtils;
import com.example.utils.ResourcesUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceProductionUtils {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final ResourcesUtils resourcesUtils;

    public ResourceProductionUtils(BuildingUpgradeUtils buildingUpgradeUtils, ResourcesUtils resourcesUtils) {
        this.buildingUpgradeUtils = buildingUpgradeUtils;
        this.resourcesUtils = resourcesUtils;
    }

    public Building generateResourceProductionBuilding(String buildingType) {
        Map<String, String> properties = new HashMap<>();
        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(1);
        properties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

        int score = buildingUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, 1);

        return Building.builder()
                .type(buildingType)
                .level(1)
                .score(score)
                .properties(properties)
                .build();
    }

    public void updateBuildingProperties(Building building) {
        Map<String, String> buildingProperties = building.getProperties();

        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(building.getLevel());
        buildingProperties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

    }

}
