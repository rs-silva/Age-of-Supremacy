package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BuildingUpgradeEventDTO {

    private UUID baseId;

    private UUID buildingId;

    private String buildingType;

    private Timestamp completionTime;

}
