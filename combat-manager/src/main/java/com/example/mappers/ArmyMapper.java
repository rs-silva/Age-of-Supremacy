package com.example.mappers;

import com.example.dto.ArmyDTO;
import com.example.models.Army;

import java.util.UUID;

public abstract class ArmyMapper {

    public Army buildDTO(UUID ownerBaseId, ArmyDTO armyDTO) {
        return Army.builder()
                .ownerBaseId(ownerBaseId)
                .units(armyDTO.getUnits())
                .build();
    }

}
