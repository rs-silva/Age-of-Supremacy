package org.example.mappers;

import org.example.dto.BuildingDTO;
import org.example.models.Building;

import java.util.Map;

public abstract class BuildingMapper {

    public static BuildingDTO buildDTO(Building building, Map<String, Integer> resourcesForNextLevel) {
        return BuildingDTO.builder()
                .type(building.getType())
                .level(building.getLevel())
                .properties(building.getProperties())
                .resourcesForNextLevel(resourcesForNextLevel)
                .build();
    }

}
