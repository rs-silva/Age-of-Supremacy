package com.example.services.buildings;

import com.example.interfaces.BuildingInterface;
import com.example.models.Building;
import com.example.utils.buildings.BuildingUpgradeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("Research Lab")
public class ResearchLabInterface implements BuildingInterface {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    public ResearchLabInterface(BuildingUpgradeUtils buildingUpgradeUtils) {
        this.buildingUpgradeUtils = buildingUpgradeUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        Map<String, String> properties = new HashMap<>();

        int score = buildingUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, 1);

        return Building.builder()
                .type(buildingType)
                .level(1)
                .score(score)
                .properties(properties)
                .build();
    }

    @Override
    public Map<String, String> getBasicProperties(Building building) {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> getAdditionalProperties(Building building) {
       return new HashMap<>();
    }
}
