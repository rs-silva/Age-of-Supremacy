package com.example.utils;

import com.example.dto.ArmySimpleDTO;
import com.example.enums.UnitNames;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public abstract class ArmyUtils {

    public static Map<String, Integer> addUnitsToArmy(Map<String, Integer> currentUnits, Map<String, Integer> unitsToAdd) {
        for (String unitName : unitsToAdd.keySet()) {
            int unitAmountToAdd = unitsToAdd.get(unitName);

            if (currentUnits.containsKey(unitName)) {
                int unitCurrentAmount = currentUnits.get(unitName);

                int unitUpdatedAmount = unitCurrentAmount + unitAmountToAdd;

                currentUnits.put(unitName, unitUpdatedAmount);
            }
            else {
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

    public static boolean doesArmyHaveAttackUnits(Map<String, Integer> armyUnits) {
        List<String> attackUnitsNames = UnitNames.getAttackUnitsNames();

        for (String attackUnitName : attackUnitsNames) {
            if (armyUnits.containsKey(attackUnitName) && armyUnits.get(attackUnitName) > 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean isArmyEmpty(Map<String, Integer> armyUnits) {
        for (String unitName : armyUnits.keySet()) {
            if (armyUnits.get(unitName) > 0) {
                return false;
            }
        }

        return true;
    }

    public static Timestamp calculateArmyArrivalTime(int originBaseX, int originBaseY, int destinationBaseX, int destinationBaseY, ArmySimpleDTO armySimpleDTO) {
        /* TODO calculate travelling time based on the bases' coordinates and the units' movement speed */
        int travellingTimeInSeconds = 5;

        return Timestamp.from(Instant.now().plusMillis(travellingTimeInSeconds * 1000));
    }

}
