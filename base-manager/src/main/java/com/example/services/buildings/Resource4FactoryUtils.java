package com.example.services.buildings;

import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Qualifier("Semiconductor Factory")
public class Resource4FactoryUtils implements BuildingUtils {

    private final ResourceProductionUtils resourceProductionUtils;

    public Resource4FactoryUtils(ResourceProductionUtils resourceProductionUtils) {
        this.resourceProductionUtils = resourceProductionUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        return resourceProductionUtils.generateResourceProductionBuilding(buildingType);
    }

    @Override
    public Map<String, String> getBasicProperties(Building building) {
        return resourceProductionUtils.getBasicProperties(building);
    }

    @Override
    public Map<String, String> getAdditionalProperties(Building building) {
        return resourceProductionUtils.getAdditionalProperties(building);
    }

}
