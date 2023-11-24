package com.example.services.buildings;

import com.example.enums.BuildingsPropertiesNames;
import com.example.interfaces.BuildingUtils;
import com.example.models.Building;
import com.example.utils.BuildingUpgradeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("Strategic Defense Center")
public class DefenseCenterUtils implements BuildingUtils {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    public DefenseCenterUtils(BuildingUpgradeUtils buildingUpgradeUtils) {
        this.buildingUpgradeUtils = buildingUpgradeUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        Map<String, String> properties = new HashMap<>();
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_AA.getLabel(), "30");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_OVERALL.getLabel(), "40");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_ANTITANK.getLabel(), "50");

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
