package com.example.utils;

import com.example.enums.BuildingNames;
import com.example.enums.BuildingsPropertiesNames;
import com.example.models.Building;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BuildingGenerationUtils {

    private final ResourcesUtils resourcesUtils;

    public BuildingGenerationUtils(ResourcesUtils resourcesUtils) {
        this.resourcesUtils = resourcesUtils;
    }

    public List<Building> generateDefaultBuildingsForNewBase() {
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

        /* Other Buildings */
        Building warehouse = generateWarehouse();
        buildingList.add(warehouse);

        Building mainBuilding = generateMainBuilding();
        buildingList.add(mainBuilding);

        return buildingList;
    }

    public Building generateResourceProductionBuilding(String type) {
        Map<String, String> properties = new HashMap<>();
        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(1);
        properties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

        return Building.builder()
                .type(type)
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

    public Building generateWarehouse() {
        Map<String, String> properties = new HashMap<>();
        Double amountOfResourcesStored = resourcesUtils.getWarehouseCapacityForLevel(1);
        properties.put(BuildingsPropertiesNames.WAREHOUSE_CAPACITY.getLabel(), amountOfResourcesStored.toString());


        return Building.builder()
                .type(BuildingNames.WAREHOUSE.getLabel())
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

    public Building generateMainBuilding() {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

        return Building.builder()
                .type(BuildingNames.MAIN_BUILDING.getLabel())
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

}
