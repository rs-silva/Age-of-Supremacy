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
        Map<String, String> properties = new HashMap<>();
        Double amountOfResourcesStored = resourcesUtils.getWarehouseCapacityForLevel(1);
        properties.put(BuildingsPropertiesNames.WAREHOUSE_CAPACITY.getLabel(), amountOfResourcesStored.toString());

        int score = buildingUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, 1);

        return Building.builder()
                .type(buildingType)
                .level(1)
                .score(score)
                .properties(properties)
                .build();
    }

    @Override
    public void updateBuildingProperties(Building building) {
        Map<String, String> buildingProperties = building.getProperties();

        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(building.getLevel());
        buildingProperties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

    }

}
