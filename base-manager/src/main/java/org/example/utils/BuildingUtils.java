package org.example.utils;

import org.example.enums.BuildingNames;
import org.example.models.Building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BuildingUtils {

    public static List<Building> generateBuildingListForNewBase() {
        List<Building> buildingList = new ArrayList<>();

        /* Resource Buildings */
        Building resource1Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_1_FACTORY.getLabel());
        buildingList.add(resource1Producer);

        Building resource2Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_2_FACTORY.getLabel());
        buildingList.add(resource2Producer);

        Building resource3Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_3_FACTORY.getLabel());
        buildingList.add(resource3Producer);

        Building resource4Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_4_FACTORY.getLabel());
        buildingList.add(resource4Producer);

        Building resource5Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_5_FACTORY.getLabel());
        buildingList.add(resource5Producer);

        Building warehouse = BuildingUtils.generateWarehouse();
        buildingList.add(warehouse);

        return buildingList;
    }

    public static Building generateResourceProductionBuilding(String type) {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

        return Building.builder()
                .type(type)
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

    public static Building generateWarehouse() {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

        return Building.builder()
                .type(BuildingNames.WAREHOUSE.getLabel())
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

}
