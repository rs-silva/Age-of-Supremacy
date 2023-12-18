package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class BaseDTO {

    private UUID id;

    private String name;

    private int x_coordinate;

    private int y_coordinate;

    private int score;

    private Map<String, Double> resources;

    private Map<String, Integer> units;

    private List<SupportArmyDTO> supportArmies;

    private List<BuildingDTO> buildingList;
}
