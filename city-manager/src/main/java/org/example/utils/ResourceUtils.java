package org.example.utils;

import org.example.config.ServerConfig;

public abstract class ResourceUtils {

    public static Double getAmountOfResourcesProducedForLevel(int level) {
        return ServerConfig.SERVER_GROWING_FACTOR * ServerConfig.BASE
                * Math.pow(ServerConfig.EXPONENTIAL, level);
    }

}
