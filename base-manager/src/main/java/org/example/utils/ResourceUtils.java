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
public class ResourceUtils {

    private final WorldConfig worldConfig;

    public ResourceUtils(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    public Map<String, Double> generateDefaultResourcesForBase() {
        Map<String, Double> resources = new HashMap<>();

        resources.put(ResourceNames.RESOURCE_1.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_2.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_3.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_4.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_5.getLabel(), worldConfig.getDEFAULT_AMOUNT());

        return resources;
    }

    public void updateBaseResources(Base base) {
        updateResource(base, ResourceNames.RESOURCE_1.getLabel(), BuildingNames.RESOURCE_1_FACTORY.getLabel());
        updateResource(base, ResourceNames.RESOURCE_2.getLabel(), BuildingNames.RESOURCE_2_FACTORY.getLabel());
        updateResource(base, ResourceNames.RESOURCE_3.getLabel(), BuildingNames.RESOURCE_3_FACTORY.getLabel());
        updateResource(base, ResourceNames.RESOURCE_4.getLabel(), BuildingNames.RESOURCE_4_FACTORY.getLabel());
        updateResource(base, ResourceNames.RESOURCE_5.getLabel(), BuildingNames.RESOURCE_5_FACTORY.getLabel());

        base.setLastResourcesUpdate(Timestamp.from(Instant.now()));
    }

    private void updateResource(Base base, String resourceName, String resourceBuildingName) {
        Map<String, Double> resources = base.getResources();
        Double resourceCurrentAmount = resources.get(resourceName);
        Timestamp lastResourcesUpdate = base.getLastResourcesUpdate();
        List<Building> buildingList = base.getBuildings();

        Float hoursDifference = calculateNumberOfHoursFromTimestampToNow(lastResourcesUpdate);

        for (Building building : buildingList) {
            if (building.getType().equals(resourceBuildingName)) {
                Integer buildingLevel = building.getLevel();
                Double amountOfResourcesProducedPerHour = getAmountOfResourcesProducedForLevel(buildingLevel);

                Double amountOfResourcesToAdd = hoursDifference * amountOfResourcesProducedPerHour;
                resources.put(resourceName, resourceCurrentAmount + amountOfResourcesToAdd);
                break;
            }
        }
    }

    private Float calculateNumberOfHoursFromTimestampToNow(Timestamp timestamp) {
        long now = System.currentTimeMillis();
        long timeDifference = now - timestamp.getTime();

        return (timeDifference / (float) (60 * 60 * 1000)); // 1 hour = 60 minutes * 60 seconds * 1000 milliseconds
    }

    /* WORLD_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    private Double getAmountOfResourcesProducedForLevel(int level) {
        return worldConfig.getWORLD_GROWING_FACTOR() * worldConfig.getBASE()
                * Math.pow(worldConfig.getEXPONENTIAL(), level);
    }

}
