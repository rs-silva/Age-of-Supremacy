package com.example.mappers;

import com.example.dto.BuildingGenerationEventDTO;
import com.example.models.BuildingGenerationEvent;

public abstract class BuildingGenerationEventMapper {

    public static BuildingGenerationEvent fromDtoToEntity(BuildingGenerationEventDTO buildingGenerationEventDTO) {
        return BuildingGenerationEvent.builder()
                .baseId(buildingGenerationEventDTO.getBaseId())
                .buildingType(buildingGenerationEventDTO.getBuildingType())
                .completionTime(buildingGenerationEventDTO.getCompletionTime())
                .build();
    }

}
