package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class BuildingDTO {

    private UUID id;

    private String type;

    private int level;

    private Map<String, String> properties;

    private Map<String, Integer> requirementsForNextLevel;

}
