package org.example.utils;

import org.example.config.WorldConfig;
import org.example.enums.BuildingNames;
import org.example.enums.ResourceNames;
import org.example.models.Base;
import org.example.models.Building;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResourcesUtils {

    private final WorldConfig worldConfig;

    public ResourcesUtils(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    public Map<String, Double> generateDefaultResourcesForBase() {
        Map<String, Double> resources = new HashMap<>();

        for (ResourceNames resourceName : ResourceNames.values()) {
            resources.put(resourceName.getLabel(), worldConfig.getRESOURCES_DEFAULT_AMOUNT());
        }

        return resources;
    }

    public void updateBaseResources(Base base) {
        Double warehouseCapacity = getWarehouseCapacity(base);
        Timestamp lastResourcesUpdate = base.getLastResourcesUpdate();

        Double hoursDifference = calculateNumberOfHoursFromTimestampToNow(lastResourcesUpdate);

        updateResource(base, ResourceNames.RESOURCE_1.getLabel(), BuildingNames.RESOURCE_1_FACTORY.getLabel(), hoursDifference, warehouseCapacity);
        updateResource(base, ResourceNames.RESOURCE_2.getLabel(), BuildingNames.RESOURCE_2_FACTORY.getLabel(), hoursDifference, warehouseCapacity);
        updateResource(base, ResourceNames.RESOURCE_3.getLabel(), BuildingNames.RESOURCE_3_FACTORY.getLabel(), hoursDifference, warehouseCapacity);
        updateResource(base, ResourceNames.RESOURCE_4.getLabel(), BuildingNames.RESOURCE_4_FACTORY.getLabel(), hoursDifference, warehouseCapacity);
        updateResource(base, ResourceNames.RESOURCE_5.getLabel(), BuildingNames.RESOURCE_5_FACTORY.getLabel(), hoursDifference, warehouseCapacity);

        base.setLastResourcesUpdate(Timestamp.from(Instant.now()));
    }

    private void updateResource(Base base, String resourceName, String resourceBuildingName, Double hoursDifference, Double warehouseCapacity) {
        Map<String, Double> resources = base.getResources();
        Double resourceCurrentAmount = resources.get(resourceName);

        Building building = getBuilding(base, resourceBuildingName);
        int buildingLevel = building.getLevel();
        Double amountOfResourcesProducedPerHour = getAmountOfResourcesProducedForLevel(buildingLevel);

        Double amountOfResourcesToAdd = hoursDifference * amountOfResourcesProducedPerHour;
        Double totalResources = resourceCurrentAmount + amountOfResourcesToAdd;

        if (totalResources > warehouseCapacity) totalResources = warehouseCapacity;

        resources.put(resourceName, totalResources);
    }

    private Double getWarehouseCapacity(Base base) {
        Building building = getBuilding(base, BuildingNames.WAREHOUSE.getLabel());

        int buildingLevel = building.getLevel();
        return getWarehouseCapacityForLevel(buildingLevel);
    }

    private Building getBuilding(Base base, String buildingName) {
        List<Building> buildingList = base.getBuildings();

        for (Building building : buildingList) {
            if (building.getType().equals(buildingName)) {
                return building;
            }
        }

        return null;
    }

    private Double calculateNumberOfHoursFromTimestampToNow(Timestamp timestamp) {
        long now = System.currentTimeMillis();
        long timeDifference = now - timestamp.getTime();

        return timeDifference / (double) (60 * 60 * 1000); // 1 hour = 60 minutes * 60 seconds * 1000 milliseconds
    }

    /* WORLD_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    private Double getAmountOfResourcesProducedForLevel(int level) {
        return worldConfig.getWORLD_GROWING_FACTOR() * worldConfig.getRESOURCES_BASE()
                * Math.pow(worldConfig.getRESOURCES_EXPONENTIAL(), level);
    }

    /* WORLD_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    private Double getWarehouseCapacityForLevel(int level) {
        return worldConfig.getWORLD_GROWING_FACTOR() * worldConfig.getWAREHOUSE_BASE()
                * Math.pow(worldConfig.getWAREHOUSE_EXPONENTIAL(), level);
    }

}
