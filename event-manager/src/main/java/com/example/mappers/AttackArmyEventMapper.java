package com.example.mappers;

import com.example.dto.ArmyMovementEventDTO;
import com.example.models.AttackArmyEvent;

public abstract class AttackArmyEventMapper {

    public static AttackArmyEvent fromDtoToEntity(ArmyMovementEventDTO armyMovementEventDTO) {
        return AttackArmyEvent.builder()
                .ownerPlayerId(armyMovementEventDTO.getOwnerPlayerId())
                .ownerBaseId(armyMovementEventDTO.getOwnerBaseId())
                .originBaseId(armyMovementEventDTO.getOriginBaseId())
                .destinationBaseId(armyMovementEventDTO.getDestinationBaseId())
                .units(armyMovementEventDTO.getUnits())
                .completionTime(armyMovementEventDTO.getArrivalTime())
                .build();
    }

}
