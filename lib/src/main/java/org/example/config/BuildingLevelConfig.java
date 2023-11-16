package org.example.config;

import lombok.Data;

import java.util.List;

@Data
public class BuildingLevelConfig {

    private int level;

    /* In seconds */
    private long constructionTime;

    private List<BuildingResourceConfig> resources;

}
