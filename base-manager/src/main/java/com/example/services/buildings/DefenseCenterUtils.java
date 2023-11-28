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
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_AA_FACTOR.getLabel(), "1");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_OVERALL_FACTOR.getLabel(), "1");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_ANTITANK_FACTOR.getLabel(), "1");

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
        Map<String, String> properties = new HashMap<>();

        int overallDefense = getOverallDefense(building.getLevel());
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_OVERALL.getLabel(), String.valueOf(overallDefense));

        return properties;
    }

    private static int getOverallDefense(int level) {
        return OVERALL_DEFENSE_VALUES[level - 1];
    }

    private static final int[] OVERALL_DEFENSE_VALUES = { 10 , 20 , 30 , 40 , 50,
                                                          60 , 70 , 80 , 90 , 100,
                                                          110, 120, 130, 140, 150,
                                                          160, 170, 180, 190, 200,
                                                          210, 220, 230, 240, 250,
                                                          260, 270, 280, 290, 300
    };

}
