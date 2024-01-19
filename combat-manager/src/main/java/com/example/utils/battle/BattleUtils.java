package com.example.utils.battle;

import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.enums.UnitNames;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.utils.ArmyUtils;
import com.example.utils.UnitConfigUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
public class BattleUtils {

    private final Map<String, Integer> frontLineUnitsLimits;

    private final ActiveDefensesPhaseUtils activeDefensesPhaseUtils;

    private final UnitConfigUtils unitConfigUtils;

    private final RestTemplate restTemplate;

    public BattleUtils(ActiveDefensesPhaseUtils activeDefensesPhaseUtils, UnitConfigUtils unitConfigUtils, RestTemplate restTemplate) {
        this.activeDefensesPhaseUtils = activeDefensesPhaseUtils;
        this.unitConfigUtils = unitConfigUtils;
        this.frontLineUnitsLimits = BattleFrontLineUnitsLimits.getFrontLineUnitsLimits();
        this.restTemplate = restTemplate;
    }

    public BattleNewUnitsForNextRoundDTO getBaseCurrentUnitsForNextRound(Battle battle) {
        UUID baseId = battle.getBaseId();

        /* TODO Remove hardcoded url */
        /* Get current units sit in the base from the base-manager module */
        String url = "http://localhost:8082/api/base/" + baseId + "/getUnitsForNextRound";
        return restTemplate.getForEntity(url, BattleNewUnitsForNextRoundDTO.class).getBody();
    }

    public BaseDefenseInformationDTO getBaseDefenseInformation(UUID baseId) {
        /* TODO Remove hardcoded url */
        /* Get defense information for this base from the base-manager module */
        String url = "http://localhost:8082/api/base/" + baseId + "/getDefenseInformation";
        return restTemplate.getForEntity(url, BaseDefenseInformationDTO.class).getBody();
    }

    public List<Army> setupFrontLine(List<Army> armies) {
        // Create a map to store the count of units for each type in the front line
        Map<String, Integer> frontLineUnitsCounter = new HashMap<>();
        List<Army> frontLineArmies = new ArrayList<>();

        // Sort armies based on some criteria (e.g., total attack power)
        //armies.sort(Comparator.comparingInt(this::calculateTotalAttackPower).reversed());

        // Iterate through armies and add units to the front line respecting type-specific limits
        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            Army newFrontLineArmy = new Army();
            newFrontLineArmy.setOwnerBaseId(army.getOwnerBaseId());
            newFrontLineArmy.setOwnerPlayerId(army.getOwnerPlayerId());
            Map<String, Integer> newFrontLineArmyUnits = new HashMap<>();

            for (Map.Entry<String, Integer> entry : armyUnits.entrySet()) {
                String unitType = entry.getKey();
                int unitAmount = entry.getValue();

                int unitTypeLimit = frontLineUnitsLimits.get(unitType);

                int unitsToAdd = Math.min(unitAmount, unitTypeLimit - frontLineUnitsCounter.getOrDefault(unitType, 0));
                if (unitsToAdd > 0) {
                    newFrontLineArmyUnits.put(unitType, unitsToAdd);

                    frontLineUnitsCounter.put(unitType, frontLineUnitsCounter.getOrDefault(unitType, 0) + unitsToAdd);

                    /* TODO Uncomment this line */
                    //armyUnits.put(unitType, unitAmount - unitsToAdd);
                }
            }

            newFrontLineArmy.setUnits(newFrontLineArmyUnits);
            frontLineArmies.add(newFrontLineArmy);
        }

        return frontLineArmies;
    }

    public int calculateAttackingPowerToBaseDefenses(List<Army> attackingArmies) {
        double attackingPower = activeDefensesPhaseUtils.calculateAttackingPowerToBaseDefenses(attackingArmies);

        double scalingFactor = getScalingFactor();
        System.out.println("SCALING FACTOR = " + scalingFactor);
        double attackingPowerWithFactor = attackingPower * scalingFactor;
        return (int) attackingPowerWithFactor;
    }

    public int getArmiesDefense(List<Army> armies) {
        double totalDefense = 0;

        for (Army army : armies) {
            totalDefense += getGroundDefense(army.getUnits());
        }

        double scalingFactor = getScalingFactor();
        System.out.println("SCALING FACTOR = " + scalingFactor);
        double defenseWithFactor = totalDefense * scalingFactor;
        return (int) defenseWithFactor;
    }

    private double getGroundDefense(Map<String, Integer> armyUnits) {
        /* Infantry + Engineers + Sniper */
        int infantryAmount = armyUnits.getOrDefault(UnitNames.GROUND_INFANTRY.getLabel(), 0);
        int engineerAmount = armyUnits.getOrDefault(UnitNames.GROUND_INFANTRY.getLabel(), 0);
        int sniperAmount = armyUnits.getOrDefault(UnitNames.GROUND_INFANTRY.getLabel(), 0);

        double infantryDefense = infantryAmount * unitConfigUtils.getUnitDefense(UnitNames.GROUND_INFANTRY.getLabel());
        double engineerDefense = engineerAmount * unitConfigUtils.getUnitDefense(UnitNames.GROUND_ENGINEER.getLabel());
        double sniperDefense = sniperAmount * unitConfigUtils.getUnitDefense(UnitNames.GROUND_SNIPER.getLabel());

        return infantryDefense + engineerDefense + sniperDefense;
    }

    public boolean checkIfFrontLinesAreFull(List<Army> attackingFrontLine, List<Army> defendingFrontLine) {
        /* Checks if both front lines have all the unit types */
        return checkIfFrontLineIsFull(attackingFrontLine) && checkIfFrontLineIsFull(defendingFrontLine);
    }

    private boolean checkIfFrontLineIsFull(List<Army> frontLine) {
        for (Army army : frontLine) {
            if (!ArmyUtils.checkIfArmyHasEveryUnitType(army.getUnits())) {
                return false;
            }
        }

        return true;
    }

    public void updateBaseDefensesHealthPoints(Battle battle, int attackingDamage) {
        int currentHealthPoints = battle.getDefenseHealthPoints();
        int updatedHealthPoints = currentHealthPoints - attackingDamage;
        battle.setDefenseHealthPoints(Math.max(updatedHealthPoints, 0));
    }

    public boolean areBaseDefensesActive(Battle battle) {
        return battle.getDefenseHealthPoints() > 0;
    }

    private double getScalingFactor() {
        /* Scaling factor = Get a scaling factor between 75% and 125% of the original attacking power value */
        double MEAN = 1;
        double STANDARD_DEVIATION = 0.07;

        return getNumberFromGaussianDistribution(MEAN, STANDARD_DEVIATION);
    }

    private double getNumberFromGaussianDistribution(double mean, double standardDeviation) {
        Random random = new Random();

        return random.nextGaussian() * standardDeviation + mean;
    }

}
