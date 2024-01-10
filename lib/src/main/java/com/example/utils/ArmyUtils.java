package com.example.utils;

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

}
