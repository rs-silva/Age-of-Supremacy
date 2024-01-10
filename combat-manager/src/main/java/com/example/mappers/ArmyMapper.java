package com.example.mappers;

import com.example.dto.ArmySimpleDTO;
import com.example.models.Army;

import java.util.UUID;

public abstract class ArmyMapper {

    public Army buildDTO(UUID ownerBaseId, ArmySimpleDTO armySimpleDTO) {
        return Army.builder()
                .ownerBaseId(ownerBaseId)
                .units(armySimpleDTO.getUnits())
                .build();
    }

}
