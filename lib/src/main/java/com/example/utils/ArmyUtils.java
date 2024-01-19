package com.example.utils;

import com.example.enums.UnitNames;

import java.util.Map;

public abstract class ArmyUtils {

    public static Map<String, Integer> addUnitsToArmy(Map<String, Integer> currentUnits, Map<String, Integer> unitsToAdd) {
        for (String unitName : unitsToAdd.keySet()) {
            int unitAmountToAdd = unitsToAdd.get(unitName);

            if (currentUnits.containsKey(unitName)) {
                int unitCurrentAmount = currentUnits.get(unitName);

                int unitUpdatedAmount = unitCurrentAmount + unitAmountToAdd;

                currentUnits.put(unitName, unitUpdatedAmount);
            } else {
                currentUnits.put(unitName, unitAmountToAdd);
            }
        }

        return currentUnits;
    }

    public static boolean checkIfArmyHasEveryUnitType(Map<String, Integer> armyUnits) {
        if (armyUnits.keySet().size() < UnitNames.values().length) {
            return false;
        }

        for (Map.Entry<String, Integer> unitInfo : armyUnits.entrySet()) {
            int unitAmount = unitInfo.getValue();

            if (unitAmount == 0) {
                return false;
            }
        }

        return true;
    }

}
