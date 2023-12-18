package com.example.mappers;

import com.example.dto.SupportArmyDTO;
import com.example.models.SupportArmy;

public abstract class SupportArmyMapper {

    public static SupportArmyDTO fromEntityToDto(SupportArmy supportArmy) {
        return SupportArmyDTO.builder()
                .id(supportArmy.getId())
                .ownerBaseId(supportArmy.getOwnerBaseId())
                .units(supportArmy.getUnits())
                .build();
    }

}
