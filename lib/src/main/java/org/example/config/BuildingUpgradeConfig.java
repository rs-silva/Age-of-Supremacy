package org.example.config;

import lombok.Data;

import java.util.List;

@Data
public class BuildingUpgradeConfig {

    private String buildingName;
    private List<BuildingLevelConfig> levels;

}