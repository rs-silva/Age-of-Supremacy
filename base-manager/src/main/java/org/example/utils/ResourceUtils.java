package org.example.utils;

import org.example.config.WorldConfig;
import org.example.enums.ResourceNames;

import java.util.HashMap;
import java.util.Map;

public class ResourceUtils {

    private final WorldConfig worldConfig;

    public ResourceUtils(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    public static Map<String, Integer> generateDefaultResourcesForBase() {
        Map<String, Integer> resources = new HashMap<>();

        resources.put(ResourceNames.RESOURCE_1.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_2.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_3.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_4.getLabel(), 1000);
        resources.put(ResourceNames.RESOURCE_5.getLabel(), 1000);

        return resources;
    }

    /* WORLD_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    public Double getAmountOfResourcesProducedForLevel(int level) {
        return worldConfig.getWORLD_GROWING_FACTOR() * worldConfig.getBASE()
                * Math.pow(worldConfig.getEXPONENTIAL(), level);
    }

}
