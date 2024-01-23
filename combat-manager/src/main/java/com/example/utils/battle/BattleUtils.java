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

        return applyScalingFactor((int) attackingPower);
    }

    public int getArmiesMetric(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        int totalMetric = 0;

        totalMetric += getArmiesGroundUnitsMetric(armies, metricFunction);
        totalMetric += getArmiesArmoredUnitsMetric(armies, metricFunction);
        totalMetric += getArmiesAirUnitsMetric(armies, metricFunction);

        return totalMetric;
    }

    public int getArmiesGroundUnitsMetric(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        double totalMetric = 0;

        /* Infantry + Engineers + Sniper */
        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            int infantryAmount = armyUnits.getOrDefault(UnitNames.GROUND_INFANTRY.getLabel(), 0);
            int engineerAmount = armyUnits.getOrDefault(UnitNames.GROUND_ENGINEER.getLabel(), 0);
            int sniperAmount = armyUnits.getOrDefault(UnitNames.GROUND_SNIPER.getLabel(), 0);

            double infantryMetric = infantryAmount * unitConfigUtils.getUnitMetric(UnitNames.GROUND_INFANTRY.getLabel(), metricFunction);
            double engineerMetric = engineerAmount * unitConfigUtils.getUnitMetric(UnitNames.GROUND_ENGINEER.getLabel(), metricFunction);
            double sniperMetric = sniperAmount * unitConfigUtils.getUnitMetric(UnitNames.GROUND_SNIPER.getLabel(), metricFunction);

            totalMetric = infantryMetric + engineerMetric + sniperMetric;
        }

        return (int) totalMetric;
    }

    public int applyScalingFactor(int value) {
        double scalingFactor = getScalingFactor();
        LOG.info("scalingFactor = {}", scalingFactor);

        return (int) (value * scalingFactor);
    }

    public int getArmiesArmoredUnitsMetric(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        double totalMetric = 0;

        /* APC + MBT + Artillery */
        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            int apcAmount = armyUnits.getOrDefault(UnitNames.ARMORED_APC.getLabel(), 0);
            int mbtAmount = armyUnits.getOrDefault(UnitNames.ARMORED_MBT.getLabel(), 0);
            int artilleryAmount = armyUnits.getOrDefault(UnitNames.ARMORED_ARTILLERY.getLabel(), 0);

            double apcMetric = apcAmount * unitConfigUtils.getUnitMetric(UnitNames.ARMORED_APC.getLabel(), metricFunction);
            double mbtMetric = mbtAmount * unitConfigUtils.getUnitMetric(UnitNames.ARMORED_MBT.getLabel(), metricFunction);
            double artilleryMetric = artilleryAmount * unitConfigUtils.getUnitMetric(UnitNames.ARMORED_ARTILLERY.getLabel(), metricFunction);

            totalMetric = apcMetric + mbtMetric + artilleryMetric;
        }

        return (int) totalMetric;
    }

    public int getArmiesAirUnitsMetric(List<Army> armies, Function<UnitDTO, Double> metricFunction) {
        double totalMetric = 0;

        /* Jet Fighter + Bomber + Recon */
        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            int jetFighterAmount = armyUnits.getOrDefault(UnitNames.AIR_FIGHTER.getLabel(), 0);
            int bomberAmount = armyUnits.getOrDefault(UnitNames.AIR_BOMBER.getLabel(), 0);
            int reconAmount = armyUnits.getOrDefault(UnitNames.AIR_RECON.getLabel(), 0);

            double jetFighterMetric = jetFighterAmount * unitConfigUtils.getUnitMetric(UnitNames.AIR_FIGHTER.getLabel(), metricFunction);
            double bomberMetric = bomberAmount * unitConfigUtils.getUnitMetric(UnitNames.AIR_BOMBER.getLabel(), metricFunction);
            double reconMetric = reconAmount * unitConfigUtils.getUnitMetric(UnitNames.AIR_RECON.getLabel(), metricFunction);

            totalMetric = jetFighterMetric + bomberMetric + reconMetric;
        }

        return (int) totalMetric;
    }

    public void calculateGroundUnitsLosses(List<Army> armies, int totalDamage) {
        /* Infantry + Engineers + Sniper */
        int infantryFrontLineLimit = frontLineUnitsLimits.get(UnitNames.GROUND_INFANTRY.getLabel());
        int engineerFrontLineLimit = frontLineUnitsLimits.get(UnitNames.GROUND_ENGINEER.getLabel());
        int sniperFrontLineLimit = frontLineUnitsLimits.get(UnitNames.GROUND_SNIPER.getLabel());
        int totalFrontLineLimit = infantryFrontLineLimit + engineerFrontLineLimit + sniperFrontLineLimit;

        double infantryFrontLineLimitPercentage = (double) infantryFrontLineLimit / totalFrontLineLimit;
        LOG.info("infantryFrontLineLimitPercentage = {}", infantryFrontLineLimitPercentage);
        double engineerFrontLineLimitPercentage = (double) engineerFrontLineLimit / totalFrontLineLimit;
        LOG.info("engineerFrontLineLimitPercentage = {}", engineerFrontLineLimitPercentage);
        double sniperFrontLineLimitPercentage = (double) sniperFrontLineLimit / totalFrontLineLimit;
        LOG.info("sniperFrontLineLimitPercentage = {}", sniperFrontLineLimitPercentage);

        int infantryDamage = (int) (totalDamage * infantryFrontLineLimitPercentage);
        int engineersDamage = (int) (totalDamage * engineerFrontLineLimitPercentage);
        int snipersDamage = (int) (totalDamage * sniperFrontLineLimitPercentage);

        double infantryHealthPoints = unitConfigUtils.getUnitMetric(UnitNames.GROUND_INFANTRY.getLabel(), UnitDTO::getHealthPoints);
        double engineerHealthPoints = unitConfigUtils.getUnitMetric(UnitNames.GROUND_ENGINEER.getLabel(), UnitDTO::getHealthPoints);
        double sniperHealthPoints = unitConfigUtils.getUnitMetric(UnitNames.GROUND_SNIPER.getLabel(), UnitDTO::getHealthPoints);

        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            if (infantryDamage > 0) {
                int infantryAmount = armyUnits.getOrDefault(UnitNames.GROUND_INFANTRY.getLabel(), 0);

                int totalInfantryLosses = (int) (infantryDamage / infantryHealthPoints);
                totalInfantryLosses = Math.min(totalInfantryLosses, infantryAmount);
                LOG.info("totalInfantryLosses = {}", totalInfantryLosses);
            }

            if (engineersDamage > 0) {
                int engineersAmount = armyUnits.getOrDefault(UnitNames.GROUND_ENGINEER.getLabel(), 0);

                int totalEngineerLosses = (int) (engineersDamage / engineerHealthPoints);
                totalEngineerLosses = Math.min(totalEngineerLosses, engineersAmount);
                LOG.info("totalEngineerLosses = {}", totalEngineerLosses);
            }

            if (snipersDamage > 0) {
                int sniperAmount = armyUnits.getOrDefault(UnitNames.GROUND_SNIPER.getLabel(), 0);

                int totalSniperLosses = (int) (snipersDamage / sniperHealthPoints);
                totalSniperLosses = Math.min(totalSniperLosses, sniperAmount);
                LOG.info("totalSniperLosses = {}", totalSniperLosses);
            }
        }
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
