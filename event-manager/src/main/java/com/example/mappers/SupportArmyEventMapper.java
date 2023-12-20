package com.example.mappers;

import com.example.dto.SupportArmyEventDTO;
import com.example.models.SupportArmyEvent;

public abstract class SupportArmyEventMapper {

    public static SupportArmyEvent fromDtoToEntity(SupportArmyEventDTO supportArmyEventDTO) {
        return SupportArmyEvent.builder()
                .ownerBaseId(supportArmyEventDTO.getOwnerBaseId())
                .originBaseId(supportArmyEventDTO.getOriginBaseId())
                .destinationBaseId(supportArmyEventDTO.getDestinationBaseId())
                .supportArmyId(supportArmyEventDTO.getSupportArmyId())
                .units(supportArmyEventDTO.getUnits())
                .completionTime(supportArmyEventDTO.getArrivalTime())
                .build();
    }

}
