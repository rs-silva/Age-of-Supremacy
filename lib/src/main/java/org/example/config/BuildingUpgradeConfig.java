package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildingUpgradeConfig {

    @JsonProperty("buildingName")
    private String buildingName;

    @JsonProperty("maxLevel")
    private int maxLevel;

    @JsonProperty("levels")
    private List<BuildingLevelConfig> levels;

}