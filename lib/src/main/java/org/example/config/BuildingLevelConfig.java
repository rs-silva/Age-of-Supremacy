package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildingLevelConfig {

    @JsonProperty("level")
    private int level;

    /* In seconds */
    @JsonProperty("constructionTime")
    private long constructionTime;

    @JsonProperty("resources")
    private List<BuildingResourceConfig> resources;

}
