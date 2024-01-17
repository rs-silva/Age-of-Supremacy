package com.example.utils.battle;

import com.example.enums.UnitNames;
import com.example.models.Army;
import com.example.utils.UnitConfigUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ActiveDefensesPhaseUtils {

    private static final double ENGINEER_DAMAGE_FACTOR = 0.3;

    private static final double ARTILLERY_DAMAGE_FACTOR = 0.8;

    private static final double BOMBER_DAMAGE_FACTOR = 1;

    private final UnitConfigUtils unitConfigUtils;

    public ActiveDefensesPhaseUtils(UnitConfigUtils unitConfigUtils) {
        this.unitConfigUtils = unitConfigUtils;
    }

    public double calculateAttackingPowerToBaseDefenses(List<Army> frontLineAttackingArmies) {
        double totalAttackPower = 0;

        /* Only engineers, artillery and bombers can damage the base defenses */
        double engineerDamage = unitConfigUtils.getUnitAttackValue(UnitNames.GROUND_ENGINEER.getLabel()) * ENGINEER_DAMAGE_FACTOR;
        double artilleryDamage = unitConfigUtils.getUnitAttackValue(UnitNames.ARMORED_ARTILLERY.getLabel()) * ARTILLERY_DAMAGE_FACTOR;
        double bomberDamage = unitConfigUtils.getUnitAttackValue(UnitNames.AIR_BOMBER.getLabel()) * BOMBER_DAMAGE_FACTOR;

        for (Army army : frontLineAttackingArmies) {
            Map<String, Integer> armyUnits = army.getUnits();

            double engineersAttackPower = calculateUnitAttackPower(armyUnits, UnitNames.GROUND_ENGINEER.getLabel(), engineerDamage);

            double artilleryAttackPower = calculateUnitAttackPower(armyUnits, UnitNames.ARMORED_ARTILLERY.getLabel(), artilleryDamage);

            double bombersAttackPower = calculateUnitAttackPower(armyUnits, UnitNames.AIR_BOMBER.getLabel(), bomberDamage);

            totalAttackPower += engineersAttackPower + artilleryAttackPower + bombersAttackPower;
        }

        return totalAttackPower;
    }

    private double calculateUnitAttackPower(Map<String, Integer> units, String unitName, double unitDamage) {
        int unitAmount = units.getOrDefault(unitName, 0);
        return unitAmount * unitDamage;
    }

}
