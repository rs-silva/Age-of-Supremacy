package com.example.utils.battle;

import com.example.enums.UnitNames;
import com.example.models.Army;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EngagementPhaseUtils {

    private final BattleUtils battleUtils;

    private final Map<String, Map<String, Double>> unitsDamageFactor;

    public EngagementPhaseUtils(BattleUtils battleUtils) {
        this.battleUtils = battleUtils;
        this.unitsDamageFactor = EngagementPhaseUnitsDamageFactor.getActiveDefensesPhaseUnitsDamageFactors();
    }

    public void calculateArmiesLosses(List<Army> attackingArmies, List<Army> defendingArmies) {
        /* Calculate damage for each unit type */
        for (String unitName : UnitNames.getAttackUnitsNames()) {

        }

    }

    private Map<String, Double> calculateArmyUnitsDamage(List<Army> armies) {
        Map<String, Double> unitsDamage = new HashMap<>();

        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            for (Map.Entry<String, Integer> unit : armyUnits.entrySet()) {
                String unitName = unit.getKey();
                int unitAmount = unit.getValue();

                int unitDamage =

            }
        }
    }

    private double getUnitDamageFactor(String attackingUnitName, String defendingUnitName) {
        Map<String, Double> unitDamageFactors = unitsDamageFactor.get(attackingUnitName);

        return unitDamageFactors.get(defendingUnitName);
    }

}
