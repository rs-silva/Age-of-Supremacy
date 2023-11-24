package com.example.services.buildings;

import com.example.enums.BuildingsPropertiesNames;
import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import com.example.utils.ResourcesUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Qualifier("Masonry Workshop")
public class Resource1FactoryUtils implements BuildingUtils {

    private final ResourceProductionUtils resourceProductionUtils;

    private final ResourcesUtils resourcesUtils;

    public Resource1FactoryUtils(ResourceProductionUtils resourceProductionUtils, ResourcesUtils resourcesUtils) {
        this.resourceProductionUtils = resourceProductionUtils;
        this.resourcesUtils = resourcesUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        return resourceProductionUtils.generateResourceProductionBuilding(buildingType);
    }

    @Override
    public void updateBuildingProperties(Building building) {
        Map<String, String> buildingProperties = building.getProperties();

        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(building.getLevel());
        buildingProperties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

    }

}
