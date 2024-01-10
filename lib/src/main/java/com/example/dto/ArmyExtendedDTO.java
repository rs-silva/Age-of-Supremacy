package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ArmyExtendedDTO {

    private UUID ownerPlayerId;

    private UUID ownerBaseId;

    private Map<String, Integer> units;

}
