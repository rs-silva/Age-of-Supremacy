package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class SupportArmyDTO {

    private UUID id;

    private UUID ownerBaseId;

    private String ownerBaseName;

    private Map<String, Integer> units;

}
