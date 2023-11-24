package com.example.mappers;

import com.example.models.Building;
import com.example.dto.BuildingDTO;

import java.util.HashMap;
import java.util.Map;

public abstract class BuildingMapper {

    public static BuildingDTO buildDTO(Building building, Map<String, String> basicProperties, Map<String, String> additionalProperties, Map<String, Integer> requirementsForNextLevel) {
        BuildingDTO buildingDTO = BuildingDTO.builder()
                .id(building.getId())
                .type(building.getType())
                .level(building.getLevel())
                .requirementsForNextLevel(requirementsForNextLevel)
                .build();

        Map<String, String> properties = new HashMap<>();

        properties.putAll(building.getProperties());
        properties.putAll(basicProperties);
        properties.putAll(additionalProperties);

        buildingDTO.setProperties(properties);

        return buildingDTO;
    }

}
