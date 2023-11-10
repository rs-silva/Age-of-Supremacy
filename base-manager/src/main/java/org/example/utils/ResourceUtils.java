package org.example.utils;

import org.example.config.WorldConfig;
import org.example.enums.ResourceNames;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResourceUtils {

    private final WorldConfig worldConfig;

    public ResourceUtils(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    public Map<String, Integer> generateDefaultResourcesForBase() {
        Map<String, Integer> resources = new HashMap<>();

        resources.put(ResourceNames.RESOURCE_1.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_2.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_3.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_4.getLabel(), worldConfig.getDEFAULT_AMOUNT());
        resources.put(ResourceNames.RESOURCE_5.getLabel(), worldConfig.getDEFAULT_AMOUNT());

        return resources;
    }

    public void updateBaseResources(Map<String, Integer> resources) {
        Integer resourceAmount = resources.get(ResourceNames.RESOURCE_1.getLabel());
        resources.put(ResourceNames.RESOURCE_1.getLabel(), resourceAmount + 100);
    }

    /* WORLD_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    public Double getAmountOfResourcesProducedForLevel(int level) {
        return worldConfig.getWORLD_GROWING_FACTOR() * worldConfig.getBASE()
                * Math.pow(worldConfig.getEXPONENTIAL(), level);
    }

}
