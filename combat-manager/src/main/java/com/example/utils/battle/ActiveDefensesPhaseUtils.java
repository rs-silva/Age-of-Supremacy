package com.example.utils.battle;

import com.example.dto.UnitDTO;
import com.example.models.Army;
import com.example.utils.UnitConfigUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ActiveDefensesPhaseUtils {

    private final Map<String, Double> unitsDamageFactor;

    private final UnitConfigUtils unitConfigUtils;

    public ActiveDefensesPhaseUtils(UnitConfigUtils unitConfigUtils) {
        this.unitsDamageFactor = ActiveDefensesPhaseUnitsDamageFactor.getActiveDefensesPhaseUnitsDamageFactors();
        this.unitConfigUtils = unitConfigUtils;
    }

    public double calculateAttackingPowerToBaseDefenses(List<Army> frontLineAttackingArmies) {
        double totalAttackPower = 0;

        for (Army army : frontLineAttackingArmies) {
            Map<String, Integer> armyUnits = army.getUnits();

            for (String unitName : armyUnits.keySet()) {
                UnitDTO unitConfig = unitConfigUtils.getUnitConfig(unitName);

                double unitAttackValue = unitConfig.getAttack();
                double unitAccuracy = unitConfig.getAccuracy();

                int unitAmount = armyUnits.get(unitName);
                double unitDamageFactor = unitsDamageFactor.get(unitName);

                double unitAttackPower = calculateUnitAttackPower(unitAmount, unitAttackValue, unitDamageFactor, unitAccuracy);

                totalAttackPower += unitAttackPower;
            }

        }

        return totalAttackPower;
    }

    private double calculateUnitAttackPower(int unitAmount, double unitAttackValue, double unitDamageFactor, double unitAccuracy) {
        return unitAmount * unitAttackValue * unitDamageFactor * unitAccuracy;
    }

}
