package com.example.mappers;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.models.BuildingUpgradeEvent;

public abstract class BuildingUpgradeEventMapper {

    public static BuildingUpgradeEvent fromDtoToEntity(BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        return BuildingUpgradeEvent.builder()
                .buildingId(buildingUpgradeEventDTO.getBuildingId())
                .completionTime(buildingUpgradeEventDTO.getCompletionTime())
                .build();
    }

}
