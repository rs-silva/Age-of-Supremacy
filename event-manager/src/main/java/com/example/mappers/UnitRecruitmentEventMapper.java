package com.example.mappers;

import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.models.UnitRecruitmentEvent;

public abstract class UnitRecruitmentEventMapper {

    public static UnitRecruitmentEvent fromDtoToEntity(UnitsRecruitmentEventDTO unitsRecruitmentEventDTO) {
        return UnitRecruitmentEvent.builder()
                .baseId(unitsRecruitmentEventDTO.getBaseId())
                .units(unitsRecruitmentEventDTO.getUnits())
                .completionTime(unitsRecruitmentEventDTO.getCompletionTime())
                .build();
    }

}
