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
        int score = buildingUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, 1);

        return Building.builder()
                .type(buildingType)
                .level(1)
                .score(score)
                .properties(new HashMap<>())
                .build();
    }

    public Map<String, String> getBasicProperties(Building building) {
        Map<String, String> additionalProperties = new HashMap<>();

        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(building.getLevel());
        additionalProperties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

        return additionalProperties;
    }

    public Map<String, String> getAdditionalProperties(Building building) {
        return new HashMap<>();
    }

}
