package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
public class BuildingDTO {

    private String type;

    private int level;

    private Map<String, String> properties;

    private Map<String, Integer> requirementsForNextLevel;

}
