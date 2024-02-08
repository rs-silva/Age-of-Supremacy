package com.example.utils.battle;

import com.example.enums.UnitNames;

import java.util.HashMap;
import java.util.Map;

public abstract class EngagementPhaseUnitsDamageFactor {

    public static double[] damageFactorsInfantry = { 1   , 2    , 0.25,
                                                     0.01, 0.003, 1   ,
                                                     0   , 0    , 0   ,
    };

    public static double[] damageFactorsEngineer = { 0.5 , 1   , 0.15,
                                                     0.1 , 0.05, 3   ,
                                                     0.01, 0.05, 0.03,
    };

    public static double[] damageFactorsSniper = { 1, 1.5, 1,
                                                   0, 0  , 0,
                                                   0, 0  , 0,
    };

    public static double[] damageFactorsAPC = { 1   , 1.5 , 1   ,
                                                1   , 0.2 , 3   ,
                                                0.01, 0.05, 0.03,
    };

    public static double[] damageFactorsMBT = { 3   , 4.5, 1.5 ,
                                                3   , 1  , 10  ,
                                                0.03, 0.1, 0.06,
    };

    public static double[] damageFactorsArtillery = { 1   , 1.5, 1,
                                                      3   , 2  , 1,
                                                      0   , 0  , 0,
    };

    public static double[] damageFactorsJetFighter = { 3   , 4.5, 1.5,
                                                       1   , 1.5, 10 ,
                                                       1   , 2  , 5  ,
    };

    public static double[] damageFactorsBomber = { 3   , 4.5, 2.5 ,
                                                   2.5 , 2  , 10  ,
                                                   0.1 , 1  , 0.25,
    };

    public static double[] damageFactorsRecon = { 0    , 0   , 0,
                                                  0    , 0   , 0,
                                                  0.001, 0.01, 1,
    };

    public static Map<String, Map<String, Double>> getActiveDefensesPhaseUnitsDamageFactors() {
        Map<String, Map<String, Double>> armyDamageFactor = new HashMap<>();
        Map<String, Double> unitDamageFactorMap;

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsInfantry);
        armyDamageFactor.put(UnitNames.GROUND_INFANTRY.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsEngineer);
        armyDamageFactor.put(UnitNames.GROUND_ENGINEER.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsSniper);
        armyDamageFactor.put(UnitNames.GROUND_SNIPER.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsAPC);
        armyDamageFactor.put(UnitNames.ARMORED_APC.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsMBT);
        armyDamageFactor.put(UnitNames.ARMORED_MBT.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsArtillery);
        armyDamageFactor.put(UnitNames.ARMORED_ARTILLERY.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsJetFighter);
        armyDamageFactor.put(UnitNames.AIR_FIGHTER.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsBomber);
        armyDamageFactor.put(UnitNames.AIR_BOMBER.getLabel(), unitDamageFactorMap);

        unitDamageFactorMap = generateDamageFactorMap(damageFactorsRecon);
        armyDamageFactor.put(UnitNames.RECON.getLabel(), unitDamageFactorMap);

        return armyDamageFactor;
    }

    private static Map<String, Double> generateDamageFactorMap(double[] damageFactors) {
        int i = 0;
        Map<String, Double> map = new HashMap<>();

        for (UnitNames unitName : UnitNames.values()) {
            map.put(unitName.getLabel(), damageFactors[i]);
            i++;
        }

        return map;
    }

}
