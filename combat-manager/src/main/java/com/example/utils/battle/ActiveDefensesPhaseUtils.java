package com.example.utils.battle;

import com.example.dto.UnitDTO;
import com.example.enums.UnitNames;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.utils.MapUtils;
import com.example.utils.UnitConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActiveDefensesPhaseUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveDefensesPhaseUtils.class);

    private final Map<String, Double> unitsDamageFactor;

    private final Map<String, Integer> frontLineUnitsLimits;

    private final UnitConfigUtils unitConfigUtils;

    public ActiveDefensesPhaseUtils(UnitConfigUtils unitConfigUtils) {
        this.frontLineUnitsLimits = BattleFrontLineUnitsLimits.getFrontLineUnitsLimits();
        this.unitsDamageFactor = ActiveDefensesPhaseUnitsDamageFactor.getActiveDefensesPhaseUnitsDamageFactors();
        this.unitConfigUtils = unitConfigUtils;
    }

    public double calculateAttackingPowerToBaseDefenses(List<Army> frontLineAttackingArmies) {
        double totalAttackPower = 0;

        for (Army army : frontLineAttackingArmies) {
            Map<String, Integer> armyUnits = army.getUnits();

            for (Map.Entry<String, Integer> unit : armyUnits.entrySet()) {
                String unitName = unit.getKey();
                int unitAmount = unit.getValue();

                UnitDTO unitConfig = unitConfigUtils.getUnitConfig(unitName);

                double unitAttackValue = unitConfig.getAttack();
                double unitAccuracy = unitConfig.getAccuracy();

                double unitDamageFactor = unitsDamageFactor.get(unitName);
                double unitAttackPower = calculateUnitAttackPower(unitAmount, unitAttackValue, unitDamageFactor, unitAccuracy);

                totalAttackPower += unitAttackPower;
            }

        }

        return totalAttackPower;
    }

    public void calculateGroundUnitsLosses(List<Army> armies, int totalDamage) {
        /* Infantry + Engineers + Sniper */
        calculateUnitsLosses(armies, UnitNames.getGroundUnitsNames(), totalDamage);
    }

    public void calculateArmoredUnitsLosses(List<Army> armies, int totalDamage) {
        /* APC + MBT + Artillery */
        calculateUnitsLosses(armies, UnitNames.getArmoredUnitsNames(), totalDamage);
    }

    public void calculateAirUnitsLosses(List<Army> armies, int totalDamage) {
        /* Jet Fighter + Bomber + Recon */
        calculateUnitsLosses(armies, UnitNames.getAirUnitsNames(), totalDamage);
    }

    public void calculateUnitsLosses(List<Army> armies, List<String> unitNames, int totalDamage) {
        Map<String, Integer> unitsFrontLineLimits = getUnitsFrontLineLimits(unitNames);

        int totalUnitsFrontLineLimit = unitsFrontLineLimits.values().stream().reduce(0, Integer::sum);

        Map<String, Double> unitsFrontLineLimitPercentage = getUnitsFrontLineLimitsPercentage(unitsFrontLineLimits, totalUnitsFrontLineLimit);

        Map<String, Integer> totalDamageToUnits = getUnitsTotalDamage(totalDamage, unitsFrontLineLimitPercentage);

        while (isAnyDamageRemaining(totalDamageToUnits) && areAnyUnitsRemaining(armies, unitNames)) {

            LOG.info("totalDamageToUnits = {}", totalDamageToUnits);

            for (Map.Entry<String, Integer> totalDamageToUnit : totalDamageToUnits.entrySet()) {
                String unitName = totalDamageToUnit.getKey();
                int damageToUnit = totalDamageToUnit.getValue();

                damageToUnit = distributeDamageAmongUnitType(armies, unitName, damageToUnit);
                totalDamageToUnits.put(unitName, damageToUnit);
            }

            totalDamageToUnits = MapUtils.shuffleMapValues(totalDamageToUnits);

        }

        LOG.info("totalDamageToUnits AFTER = {}", totalDamageToUnits);

    }

    public void updateBaseDefensesHealthPoints(Battle battle, int attackingDamage) {
        int currentHealthPoints = battle.getDefenseHealthPoints();
        int updatedHealthPoints = currentHealthPoints - attackingDamage;
        battle.setDefenseHealthPoints(Math.max(updatedHealthPoints, 0));
    }

    private int distributeDamageAmongUnitType(List<Army> armies, String unitName, int damageToUnit) {
        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            if (damageToUnit > 0) {
                int unitAmount = armyUnits.getOrDefault(unitName, 0);
                double unitHealthPoints = unitConfigUtils.getUnitMetric(unitName, UnitDTO::getHealthPoints);

                int totalUnitLosses = (int) (damageToUnit / unitHealthPoints);
                totalUnitLosses = Math.min(totalUnitLosses, unitAmount);

                LOG.info("Total {} losses = {}", unitName, totalUnitLosses);
                armyUnits.put(unitName, unitAmount - totalUnitLosses);

                damageToUnit = (int) (damageToUnit - (totalUnitLosses * unitHealthPoints));
                if (damageToUnit < unitHealthPoints) {
                    return 0;
                }

            }

            //LOG.info("damageToUnit = {}", damageToUnit);
        }

        return damageToUnit;
    }

    private Map<String, Integer> getUnitsFrontLineLimits(List<String> unitNames) {
        Map<String, Integer> unitsFrontLineLimit = new HashMap<>();

        for (String unitName : unitNames) {
            int unitFrontLineLimit = frontLineUnitsLimits.get(unitName);

            unitsFrontLineLimit.put(unitName, unitFrontLineLimit);
        }

        return unitsFrontLineLimit;
    }

    private Map<String, Double> getUnitsFrontLineLimitsPercentage(Map<String, Integer> unitsFrontLineLimits, int totalFrontLineLimit) {
        Map<String, Double> unitsFrontLineLimitPercentage = new HashMap<>();

        for (Map.Entry<String, Integer> unitFrontLineLimit : unitsFrontLineLimits.entrySet()) {
            String unitName = unitFrontLineLimit.getKey();
            int unitLimit = unitFrontLineLimit.getValue();

            double unitFrontLineLimitPercentage = (double) unitLimit / totalFrontLineLimit;
            //LOG.info("Front Line limit percentage for {} = {}", unitName, unitFrontLineLimitPercentage);
            unitsFrontLineLimitPercentage.put(unitName, unitFrontLineLimitPercentage);
        }

        return unitsFrontLineLimitPercentage;
    }

    private Map<String, Integer> getUnitsTotalDamage(int totalDamage, Map<String, Double> unitsFrontLineLimitPercentage) {
        Map<String, Integer> unitsTotalDamage = new HashMap<>();

        for (Map.Entry<String, Double> unitFrontLineLimitPercentage : unitsFrontLineLimitPercentage.entrySet()) {
            String unitName = unitFrontLineLimitPercentage.getKey();
            double unitPercentage = unitFrontLineLimitPercentage.getValue();

            int unitDamage = (int) (unitPercentage * totalDamage);
            unitsTotalDamage.put(unitName, unitDamage);
        }

        return unitsTotalDamage;
    }

    private boolean isAnyDamageRemaining(Map<String, Integer> damageToUnits) {
        for (Map.Entry<String, Integer> damageToUnit : damageToUnits.entrySet()) {
            int damage = damageToUnit.getValue();
            if (damage > 0) return true;
        }

        return false;
    }

    private boolean areAnyUnitsRemaining(List<Army> armies, List<String> unitNames) {
        for (Army army : armies) {

            for (String unitName : unitNames) {
                Map<String, Integer> armyUnits = army.getUnits();

                int unitAmount = armyUnits.getOrDefault(unitName, 0);
                if (unitAmount > 0) return true;
            }

        }

        return false;
    }

    private double calculateUnitAttackPower(int unitAmount, double unitAttackValue, double unitDamageFactor, double unitAccuracy) {
        return unitAmount * unitAttackValue * unitDamageFactor * unitAccuracy;
    }

}
