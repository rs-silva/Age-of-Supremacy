package org.example.utils;

import org.example.config.WorldConfig;

public class ResourceUtils {

    private final WorldConfig worldConfig;

    public ResourceUtils(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    /* WORLD_GROWING_FACTOR * BASE * (EXPONENTIAL ^ LEVEL_OF_BUILDING)*/
    public Double getAmountOfResourcesProducedForLevel(int level) {
        return worldConfig.getWORLD_GROWING_FACTOR() * worldConfig.getBASE()
                * Math.pow(worldConfig.getEXPONENTIAL(), level);
    }

}
