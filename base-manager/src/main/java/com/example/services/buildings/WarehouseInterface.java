package com.example.services.buildings;

import com.example.enums.BuildingsPropertiesNames;
import com.example.interfaces.BuildingInterface;
import com.example.models.Building;
import com.example.utils.buildings.BuildingUpgradeUtils;
import com.example.utils.ResourcesUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("Warehouse")
public class WarehouseInterface implements BuildingInterface {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final ResourcesUtils resourcesUtils;

    public WarehouseInterface(BuildingUpgradeUtils buildingUpgradeUtils, ResourcesUtils resourcesUtils) {
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

        Double amountOfResourcesStored = resourcesUtils.getWarehouseCapacityForLevel(building.getLevel());
        additionalProperties.put(BuildingsPropertiesNames.WAREHOUSE_CAPACITY.getLabel(), amountOfResourcesStored.toString());

        return additionalProperties;
    }

    @Override
    public Map<String, String> getAdditionalProperties(Building building) {
       return new HashMap<>();
    }

}
