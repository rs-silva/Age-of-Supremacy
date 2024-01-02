package com.example.mappers;

import com.example.dto.SupportArmyDTO;
import com.example.models.SupportArmy;

public abstract class SupportArmyMapper {

    public static SupportArmyDTO buildDTO(SupportArmy supportArmy, String ownerBaseName) {
        return SupportArmyDTO.builder()
                .id(supportArmy.getId())
                .ownerBaseId(supportArmy.getOwnerBaseId())
                .ownerBaseName(ownerBaseName)
                .units(supportArmy.getUnits())
                .build();
    }

}
