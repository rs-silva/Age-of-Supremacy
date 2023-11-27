package com.example.services.buildings;

import com.example.enums.BuildingsPropertiesNames;
import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import com.example.utils.BuildingUpgradeUtils;
import com.example.utils.ResourcesUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("Warehouse")
public class WarehouseUtils implements BuildingUtils {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final ResourcesUtils resourcesUtils;

    public WarehouseUtils(BuildingUpgradeUtils buildingUpgradeUtils, ResourcesUtils resourcesUtils) {
        this.buildingUpgradeUtils = buildingUpgradeUtils;
        this.resourcesUtils = resourcesUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        int score = buildingUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, 1);

        return Building.builder()
                .type(buildingType)
                .level(1)
                .score(score)
                .properties(new HashMap<>())
                .build();
    }

    @Override
    public Map<String, String> getBasicProperties(Building building) {
        Map<String, String> additionalProperties = new HashMap<>();

        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(building.getLevel());
        additionalProperties.put(BuildingsPropertiesNames.WAREHOUSE_CAPACITY.getLabel(), amountOfResourcesProduced.toString());

        return additionalProperties;
    }

    @Override
    public Map<String, String> getAdditionalProperties(Building building) {
       return new HashMap<>();
    }

}
