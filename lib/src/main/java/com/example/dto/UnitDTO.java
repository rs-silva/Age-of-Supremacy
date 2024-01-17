package com.example.dto;

import com.example.config.ResourceConfig;
import lombok.Data;

import java.util.List;

@Data
public class UnitDTO {

    private String unitName;

    private double healthPoints;

    private double movementSpeed;

    private double attack;

    private double defense;

    private double accuracy;

    private int recruitmentTime;

    private List<ResourceConfig> resources;

}
