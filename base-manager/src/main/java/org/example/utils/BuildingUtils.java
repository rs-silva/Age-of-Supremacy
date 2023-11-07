package org.example.utils;

import org.example.models.Building;

import java.util.HashMap;
import java.util.Map;

public abstract class BuildingUtils {

    public static Building generateNewResourceBuilding(String type) {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

        return Building.builder()
                .type(type)
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

}
