package com.example.mappers;

import com.example.models.Building;
import com.example.dto.BuildingDTO;

import java.util.Map;

public abstract class BuildingMapper {

    public static BuildingDTO buildDTO(Building building, Map<String, Integer> requirementsForNextLevel) {
        return BuildingDTO.builder()
                .id(building.getId())
                .type(building.getType())
                .level(building.getLevel())
                .properties(building.getProperties())
                .requirementsForNextLevel(requirementsForNextLevel)
                .build();
    }

}
