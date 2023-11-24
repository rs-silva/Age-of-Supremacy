package com.example.services.buildings;

import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("Steel Forge")
public class Resource2FactoryUtils implements BuildingUtils {

    private final ResourceProductionUtils resourceProductionUtils;


    public Resource2FactoryUtils(ResourceProductionUtils resourceProductionUtils) {
        this.resourceProductionUtils = resourceProductionUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        return resourceProductionUtils.generateResourceProductionBuilding(buildingType);
    }

    @Override
    public void updateBuildingProperties(Building building) {
        resourceProductionUtils.updateBuildingProperties(building);
    }

}
