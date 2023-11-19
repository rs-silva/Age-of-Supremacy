package org.example.mappers;

import org.example.dto.BuildingDTO;
import org.example.models.Building;

import java.util.Map;

public abstract class BuildingMapper {

    public static BuildingDTO buildDTO(Building building, Map<String, Integer> requirementsForNextLevel) {
        return BuildingDTO.builder()
                .type(building.getType())
                .level(building.getLevel())
                .properties(building.getProperties())
                .requirementsForNextLevel(requirementsForNextLevel)
                .build();
    }

}
