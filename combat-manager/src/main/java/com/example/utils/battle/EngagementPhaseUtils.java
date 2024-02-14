package com.example.utils.battle;

import com.example.dto.UnitDTO;
import com.example.models.Army;
import com.example.utils.MapUtils;
import com.example.utils.UnitConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EngagementPhaseUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EngagementPhaseUtils.class);

    private final BattleUtils battleUtils;

    private final UnitConfigUtils unitConfigUtils;

    private final Map<String, Map<String, Double>> unitsDamageFactor;

    public EngagementPhaseUtils(BattleUtils battleUtils, UnitConfigUtils unitConfigUtils) {
        this.battleUtils = battleUtils;
        this.unitConfigUtils = unitConfigUtils;
        this.unitsDamageFactor = EngagementPhaseUnitsDamageFactor.getActiveDefensesPhaseUnitsDamageFactors();
    }

    public void calculateArmiesLosses(List<Army> attackingArmies, List<Army> defendingArmies) {
        Map<String, Integer> attackingUnitsDamage = calculateArmyUnitsDamage(attackingArmies);
        Map<String, Integer> defendingUnitsDamage = calculateArmyUnitsDamage(defendingArmies);

        LOG.info("attackingUnitsDamage = {}", attackingUnitsDamage);
        LOG.info("defendingUnitsDamage = {}", defendingUnitsDamage);

    }

    private Map<String, Integer> calculateArmyUnitsDamage(List<Army> armies) {
        /* Calculate damage for each unit type */
        Map<String, Integer> unitsDamage = new HashMap<>();

        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            for (Map.Entry<String, Integer> unit : armyUnits.entrySet()) {
                String unitName = unit.getKey();
                int unitAmount = unit.getValue();

                UnitDTO unitConfig = unitConfigUtils.getUnitConfig(unitName);

                double unitAttackValue = unitConfig.getAttack();
                double unitAccuracy = unitConfig.getAccuracy();

                int unitDamage = (int) battleUtils.calculateUnitAttackPower(unitAmount, unitAttackValue, unitAccuracy);

                unitDamage = battleUtils.applyScalingFactor(unitDamage);
                unitsDamage = MapUtils.addValuesToMap(unitsDamage, unitName, unitDamage);
            }
        }

        return unitsDamage;
    }

    private void updateUnitDamage(Map<String, Double> unitsDamage, String unitName, double unitDamageToAdd) {
        if (unitsDamage.containsKey(unitName)) {
            double currentUnitDamage = unitsDamage.get(unitName);

            double updatedUnitDamage = currentUnitDamage + unitDamageToAdd;

            unitsDamage.put(unitName, updatedUnitDamage);
        }
        else {
            unitsDamage.put(unitName, unitDamageToAdd);
        }
    }

    private double getUnitDamageFactor(String attackingUnitName, String defendingUnitName) {
        Map<String, Double> unitDamageFactors = unitsDamageFactor.get(attackingUnitName);

        return unitDamageFactors.get(defendingUnitName);
    }

}
