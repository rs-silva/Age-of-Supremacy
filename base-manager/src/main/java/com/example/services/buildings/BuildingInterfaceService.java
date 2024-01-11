package com.example.services.buildings;

import com.example.interfaces.BuildingInterface;
import com.example.models.Building;
import lombok.Data;

import java.util.Map;

@Data
public class BuildingInterfaceService {

    private Map<String, BuildingInterface> buildingInterfacesMap;

    public Building generateBuilding(String buildingType) {
        BuildingInterface buildingInterface = buildingInterfacesMap.get(buildingType);

        if (buildingInterface != null) {
            return buildingInterface.generateBuilding(buildingType);
        } else {
            throw new IllegalArgumentException("Unsupported building type: " + buildingType);
        }
    }

    public Map<String, String> getBasicProperties(Building building) {
        String buildingType = building.getType();
        BuildingInterface buildingInterface = buildingInterfacesMap.get(buildingType);

        if (buildingInterface != null) {
            return buildingInterface.getBasicProperties(building);
        } else {
            throw new IllegalArgumentException("Unsupported building type: " + buildingType);
        }
    }

    public Map<String, String> getAdditionalProperties(Building building) {
        String buildingType = building.getType();
        BuildingInterface buildingInterface = buildingInterfacesMap.get(buildingType);

        if (buildingInterface != null) {
            return buildingInterface.getAdditionalProperties(building);
        } else {
            throw new IllegalArgumentException("Unsupported building type: " + buildingType);
        }
    }

}
