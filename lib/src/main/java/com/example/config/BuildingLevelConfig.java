package com.example.config;

import lombok.Data;

import java.util.List;

@Data
public class BuildingLevelConfig {

    private int level;

    /* In seconds */
    private int constructionTime;

    private int score;

    private List<ResourceConfig> resources;

}
