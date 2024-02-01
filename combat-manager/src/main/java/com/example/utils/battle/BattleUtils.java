package com.example.utils.battle;

import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.dto.UnitDTO;
import com.example.enums.UnitNames;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.utils.ArmyUtils;
import com.example.utils.UnitConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BattleUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BattleUtils.class);

    private final Map<String, Integer> frontLineUnitsLimits;

    private final ActiveDefensesPhaseUtils activeDefensesPhaseUtils;

    private final EngagementPhaseUtils engagementPhaseUtils;

    private final UnitConfigUtils unitConfigUtils;

    private final RestTemplate restTemplate;

    public BattleUtils(ActiveDefensesPhaseUtils activeDefensesPhaseUtils, EngagementPhaseUtils engagementPhaseUtils, UnitConfigUtils unitConfigUtils, RestTemplate restTemplate) {
        this.activeDefensesPhaseUtils = activeDefensesPhaseUtils;
        this.engagementPhaseUtils = engagementPhaseUtils;
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

                    /* Remove from original army */
                    armyUnits.put(unitType, unitAmount - unitsToAdd);
                }
            }

            newFrontLineArmy.setUnits(newFrontLineArmyUnits);
            frontLineArmies.add(newFrontLineArmy);
        }

        return frontLineArmies;
    }

    public int calculateAttackingPowerToBaseDefenses(List<Army> attackingArmies) {
        double attackingPower = activeDefensesPhaseUtils.calculateAttackingPowerToBaseDefenses(attackingArmies);

        return applyScalingFactor((int) attackingPower);
    }

    public int getArmiesMetric(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        int totalMetric = 0;

        totalMetric += getArmiesGroundUnitsMetricTotal(armies, metricFunction);
        totalMetric += getArmiesArmoredUnitsMetricTotal(armies, metricFunction);
        totalMetric += getArmiesAirUnitsMetricTotal(armies, metricFunction);

        return totalMetric;
    }

    public int applyScalingFactor(int value) {
        double scalingFactor = getScalingFactor();
        LOG.info("scalingFactor = {}", scalingFactor);

        return (int) (value * scalingFactor);
    }

    public int getArmiesGroundUnitsMetricTotal(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        /* Infantry + Engineers + Sniper */
        return getUnitsMetricTotal(armies, UnitNames.getGroundUnitsNames(), metricFunction);
    }

    public int getArmiesArmoredUnitsMetricTotal(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        /* APC + MBT + Artillery */
        return getUnitsMetricTotal(armies, UnitNames.getArmoredUnitsNames(), metricFunction);
    }

    public int getArmiesAirUnitsMetricTotal(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        /* Jet Fighter + Bomber + Recon */
        return getUnitsMetricTotal(armies, UnitNames.getAirUnitsNames(), metricFunction);
    }

    public int getUnitsMetricTotal(List<Army> armies, List<String> unitNames, Function<UnitDTO, Double> metricFunction) {
        double totalMetric = 0;

        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            for (String unitName : unitNames) {
                int unitAmount = armyUnits.getOrDefault(unitName, 0);

                double unitMetric = unitAmount * unitConfigUtils.getUnitMetric(unitName, metricFunction);

                totalMetric += unitMetric;
            }

        }

        return (int) totalMetric;
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
