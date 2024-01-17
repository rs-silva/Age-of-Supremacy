package com.example.services.buildings;

import com.example.enums.BuildingsPropertiesNames;
import com.example.interfaces.BuildingInterface;
import com.example.models.Building;
import com.example.utils.buildings.BuildingUpgradeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("Strategic Defense Center")
public class DefenseCenterInterface implements BuildingInterface {

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    public DefenseCenterInterface(BuildingUpgradeUtils buildingUpgradeUtils) {
        this.buildingUpgradeUtils = buildingUpgradeUtils;
    }

    @Override
    public Building generateBuilding(String buildingType) {
        Map<String, String> properties = new HashMap<>();
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_GROUND_FACTOR.getLabel(), "1");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_ANTITANK_FACTOR.getLabel(), "1");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_AA_FACTOR.getLabel(), "1");
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_HEALTH_POINTS_FACTOR.getLabel(), "1");

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

        int overallDefense = getGroundDefense(building);
        int antitankDefense = getAntiTankDefense(building);
        int aaDefense = getAADefense(building);
        int healthPoints = getHealthPoints(building);

        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_GROUND_POWER.getLabel(), String.valueOf(overallDefense));
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_ANTITANK_POWER.getLabel(), String.valueOf(antitankDefense));
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_AA_POWER.getLabel(), String.valueOf(aaDefense));
        properties.put(BuildingsPropertiesNames.DEFENSE_CENTER_HEALTH_POINTS.getLabel(), String.valueOf(healthPoints));

        return properties;
    }

    private int getGroundDefense(Building building) {
        int buildingLevel = building.getLevel();

        int defense_factor = Integer.parseInt(building.getProperties().get(BuildingsPropertiesNames.DEFENSE_CENTER_GROUND_FACTOR.getLabel()));
        int base_defense = GROUND_BASE_DEFENSE_VALUES[buildingLevel - 1];

        return defense_factor * base_defense;
    }

    private int getAntiTankDefense(Building building) {
        int buildingLevel = building.getLevel();

        int defense_factor = Integer.parseInt(building.getProperties().get(BuildingsPropertiesNames.DEFENSE_CENTER_ANTITANK_FACTOR.getLabel()));
        int base_defense = ANTITANK_BASE_DEFENSE_VALUES[buildingLevel - 1];

        return defense_factor * base_defense;
    }

    private int getAADefense(Building building) {
        int buildingLevel = building.getLevel();

        int defense_factor = Integer.parseInt(building.getProperties().get(BuildingsPropertiesNames.DEFENSE_CENTER_AA_FACTOR.getLabel()));
        int base_defense = AA_BASE_DEFENSE_VALUES[buildingLevel - 1];

        return defense_factor * base_defense;
    }

    private static int getHealthPoints(Building building) {
        int buildingLevel = building.getLevel();

        int defense_factor = Integer.parseInt(building.getProperties().get(BuildingsPropertiesNames.DEFENSE_CENTER_HEALTH_POINTS_FACTOR.getLabel()));
        int base_defense = BASE_HEALTH_POINTS[buildingLevel - 1];

        return defense_factor * base_defense;
    }

    private static final int[] GROUND_BASE_DEFENSE_VALUES = { 10 , 20 , 30 , 40 , 50,
                                                              60 , 70 , 80 , 90 , 100,
                                                              110, 120, 130, 140, 150,
                                                              160, 170, 180, 190, 200,
                                                              210, 220, 230, 240, 250,
                                                              260, 270, 280, 290, 300
    };

    private static final int[] ANTITANK_BASE_DEFENSE_VALUES = { 10 , 20 , 30 , 40 , 50,
                                                                60 , 70 , 80 , 90 , 100,
                                                                110, 120, 130, 140, 150,
                                                                160, 170, 180, 190, 200,
                                                                210, 220, 230, 240, 250,
                                                                260, 270, 280, 290, 300
    };

    private static final int[] AA_BASE_DEFENSE_VALUES = { 10 , 20 , 30 , 40 , 50,
                                                          60 , 70 , 80 , 90 , 100,
                                                          110, 120, 130, 140, 150,
                                                          160, 170, 180, 190, 200,
                                                          210, 220, 230, 240, 250,
                                                          260, 270, 280, 290, 300
    };

    private static final int[] BASE_HEALTH_POINTS = { 120  , 145  , 175  , 210  , 260,
                                                      320  , 385  , 470  , 570  , 690,
                                                      830  , 1000 , 1210 , 1450 , 1750,
                                                      2130 , 2590 , 3150 , 3825 , 4650,
                                                      5650 , 6850 , 8335 , 10130, 12295,
                                                      14995, 18215, 22150, 26900, 32500
    };

}
