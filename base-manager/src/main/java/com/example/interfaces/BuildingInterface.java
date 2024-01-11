package com.example.interfaces;

import com.example.models.Building;

import java.util.Map;

public interface BuildingInterface {

    Building generateBuilding(String buildingType);

    Map<String, String> getBasicProperties(Building building);

    Map<String, String> getAdditionalProperties(Building building);

}