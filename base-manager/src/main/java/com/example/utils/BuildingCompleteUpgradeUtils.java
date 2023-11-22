package com.example.utils;

import com.example.enums.BuildingNames;
import com.example.enums.BuildingsPropertiesNames;
import com.example.models.Building;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BuildingCompleteUpgradeUtils {

    private final ResourcesUtils resourcesUtils;

    private final BuildingRequestUpgradeUtils buildingRequestUpgradeUtils;

    public BuildingCompleteUpgradeUtils(ResourcesUtils resourcesUtils, BuildingRequestUpgradeUtils buildingRequestUpgradeUtils) {
        this.resourcesUtils = resourcesUtils;
        this.buildingRequestUpgradeUtils = buildingRequestUpgradeUtils;
    }

    public void levelUpBuilding(Building building) {
        String buildingType = building.getType();
        int buildingCurrentLevel = building.getLevel();

        building.setLevel(buildingCurrentLevel + 1);

        int score = buildingRequestUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, building.getLevel());
        building.setScore(score);

        /* Check if it is a resource production building */
        Map<String, String> buildingProperties = building.getProperties();
        boolean isAResourceProductionBuilding = buildingProperties.containsKey(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel());

        if (isAResourceProductionBuilding) {
            Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(building.getLevel());
            buildingProperties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());
        }

        else if (buildingType.equals(BuildingNames.WAREHOUSE.getLabel())){
            Double amountOfResourcesStored = resourcesUtils.getWarehouseCapacityForLevel(building.getLevel());
            buildingProperties.put(BuildingsPropertiesNames.WAREHOUSE_CAPACITY.getLabel(), amountOfResourcesStored.toString());
        }

    }

}
