package com.example.dto;

import com.example.config.ResourceConfig;
import lombok.Data;

import java.util.List;

@Data
public class UnitDTO {

    private String unitName;

    private int healthPoints;

    private double movementSpeed;

    private int attack;

    private int defense;

    private int recruitmentTime;

    private List<ResourceConfig> resourceRequirements;

}
