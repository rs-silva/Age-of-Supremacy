package org.example.config;

import lombok.Data;

import java.util.List;

@Data
public class BuildingLevelConfig {

    private int level;
    private long constructionTime;
    private List<BuildingResourceConfig> resources;

}
