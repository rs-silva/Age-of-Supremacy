package com.example.utils.battle;

import com.example.enums.UnitNames;

import java.util.HashMap;
import java.util.Map;

public abstract class EngagementPhaseUnitsDamageFactor {

    public static double[] damageFactorsInfantry = { 1   , 2    , 0.25,
                                                     0.01, 0.003, 0.5 ,
                                                     0   , 0    , 0   ,
    };

    public static double[] damageFactorsEngineer = { 0.6 , 1   , 0.15,
                                                     0.1 , 0.05, 3   ,
                                                     0.01, 0.05, 0.03,
    };

    public static double[] damageFactorsSniper = { 2, 1.5, 1,
                                                   0, 0  , 0,
                                                   0, 0  , 0,
    };

    public static Map<String, Map<String, Double>> getActiveDefensesPhaseUnitsDamageFactors() {
        Map<String, Map<String, Double>> unitsDamageFactor = new HashMap<>();
        int i = 0;

        Map<String, Double> infantry = new HashMap<>();
        for (UnitNames unitName : UnitNames.values()) {
            infantry.put(unitName.getLabel(), damageFactorsInfantry[i]);
            i++;
        }
        unitsDamageFactor.put(UnitNames.GROUND_INFANTRY.getLabel(), infantry);

        i = 0;
        Map<String, Double> engineer = new HashMap<>();
        for (UnitNames unitName : UnitNames.values()) {
            infantry.put(unitName.getLabel(), damageFactorsEngineer[i]);
            i++;
        }
        unitsDamageFactor.put(UnitNames.GROUND_ENGINEER.getLabel(), engineer);

        i = 0;
        Map<String, Double> sniper = new HashMap<>();
        for (UnitNames unitName : UnitNames.values()) {
            infantry.put(unitName.getLabel(), damageFactorsSniper[i]);
            i++;
        }
        unitsDamageFactor.put(UnitNames.GROUND_SNIPER.getLabel(), sniper);


        return unitsDamageFactor;
    }

}
