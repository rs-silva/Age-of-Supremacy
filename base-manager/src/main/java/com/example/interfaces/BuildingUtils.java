package com.example.interfaces;

import com.example.models.Building;

public interface BuildingUtils {

    Building generateBuilding(String buildingType);

    void updateBuildingProperties(Building building);

}