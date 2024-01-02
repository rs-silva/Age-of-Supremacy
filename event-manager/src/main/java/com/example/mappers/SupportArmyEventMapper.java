package com.example.mappers;

import com.example.dto.ArmyMovementEventDTO;
import com.example.models.SupportArmyEvent;

public abstract class SupportArmyEventMapper {

    public static SupportArmyEvent fromDtoToEntity(ArmyMovementEventDTO armyMovementEventDTO) {
        return SupportArmyEvent.builder()
                .ownerPlayerId(armyMovementEventDTO.getOwnerPlayerId())
                .ownerBaseId(armyMovementEventDTO.getOwnerBaseId())
                .originBaseId(armyMovementEventDTO.getOriginBaseId())
                .destinationBaseId(armyMovementEventDTO.getDestinationBaseId())
                .units(armyMovementEventDTO.getUnits())
                .completionTime(armyMovementEventDTO.getArrivalTime())
                .build();
    }

}
