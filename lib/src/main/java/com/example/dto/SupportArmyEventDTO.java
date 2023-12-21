package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupportArmyEventDTO {

    private UUID ownerBaseId;

    private UUID originBaseId;

    private UUID destinationBaseId;

    private Map<String, Integer> units;

    private Timestamp arrivalTime;

}
