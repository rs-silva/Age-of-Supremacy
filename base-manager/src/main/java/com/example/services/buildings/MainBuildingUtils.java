package com.example.services.buildings;

import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import com.example.utils.BuildingUpgradeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("Headquarters")
public class MainBuildingUtils implements BuildingUtils {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    public MainBuildingUtils(BuildingUpgradeUtils buildingUpgradeUtils) {
        this.buildingUpgradeUtils = buildingUpgradeUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

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
        return null;
    }

    @Override
    public Map<String, String> getAdditionalProperties(Building building) {
        return null;
    }

}
