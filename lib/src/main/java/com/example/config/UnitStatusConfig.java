package com.example.config;

import lombok.Data;

import java.util.List;

@Data
public class UnitStatusConfig {

    private String unitName;

    private int healthPoints;

    private int movementSpeed;

    private int attack;

    private int defense;

    private int recruitmentTime;

    private List<ResourceConfig> resources;

}
