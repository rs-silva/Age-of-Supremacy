package com.example.services.buildings;

import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import lombok.Data;

import java.util.Map;

@Data
public class BuildingUtilsService {

    private Map<String, BuildingUtils> buildingUtilsMap;

    public Building generateBuilding(String buildingType) {
        BuildingUtils buildingUtils = buildingUtilsMap.get(buildingType);

        if (buildingUtils != null) {
            return buildingUtils.generateBuilding(buildingType);
        } else {
            throw new IllegalArgumentException("Unsupported building type: " + buildingType);
        }
    }

    public Map<String, String> getBasicProperties(Building building) {
        String buildingType = building.getType();
        BuildingUtils buildingUtils = buildingUtilsMap.get(buildingType);

        if (buildingUtils != null) {
            return buildingUtils.getBasicProperties(building);
        } else {
            throw new IllegalArgumentException("Unsupported building type: " + buildingType);
        }
    }

    public Map<String, String> getAdditionalProperties(Building building) {
        String buildingType = building.getType();
        BuildingUtils buildingUtils = buildingUtilsMap.get(buildingType);

        if (buildingUtils != null) {
            return buildingUtils.getAdditionalProperties(building);
        } else {
            throw new IllegalArgumentException("Unsupported building type: " + buildingType);
        }
    }

}
