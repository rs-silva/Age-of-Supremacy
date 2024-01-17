package com.example.utils.battle;

import com.example.enums.UnitNames;

import java.util.HashMap;
import java.util.Map;

public class ActiveDefensesPhaseUnitsDamageFactor {

    private static final double GROUND_INFANTRY_DAMAGE_FACTOR = 0.001;

    private static final double GROUND_ENGINEER_DAMAGE_FACTOR = 0.25;

    private static final double GROUND_SNIPER_DAMAGE_FACTOR = 0.003;

    private static final double ARMORED_APC_DAMAGE_FACTOR = 0.15;

    private static final double ARMORED_MBT_DAMAGE_FACTOR = 0.4;

    private static final double ARMORED_ARTILLERY_DAMAGE_FACTOR = 1;

    private static final double AIR_RECON_DAMAGE_FACTOR = 0;

    private static final double AIR_FIGHTER_DAMAGE_FACTOR = 0.3;

    private static final double AIR_BOMBER_DAMAGE_FACTOR = 0.75;

    public static Map<String, Double> getActiveDefensesPhaseUnitsDamageFactors() {
        Map<String, Double> unitsDamageFactor = new HashMap<>();

        unitsDamageFactor.put(UnitNames.GROUND_INFANTRY.getLabel(), GROUND_INFANTRY_DAMAGE_FACTOR);
        unitsDamageFactor.put(UnitNames.GROUND_ENGINEER.getLabel(), GROUND_ENGINEER_DAMAGE_FACTOR);
        unitsDamageFactor.put(UnitNames.GROUND_SNIPER.getLabel(), GROUND_SNIPER_DAMAGE_FACTOR);

        unitsDamageFactor.put(UnitNames.ARMORED_APC.getLabel(), ARMORED_APC_DAMAGE_FACTOR);
        unitsDamageFactor.put(UnitNames.ARMORED_MBT.getLabel(), ARMORED_MBT_DAMAGE_FACTOR);
        unitsDamageFactor.put(UnitNames.ARMORED_ARTILLERY.getLabel(), ARMORED_ARTILLERY_DAMAGE_FACTOR);

        unitsDamageFactor.put(UnitNames.AIR_FIGHTER.getLabel(), AIR_FIGHTER_DAMAGE_FACTOR);
        unitsDamageFactor.put(UnitNames.AIR_BOMBER.getLabel(), AIR_BOMBER_DAMAGE_FACTOR);
        unitsDamageFactor.put(UnitNames.AIR_RECON.getLabel(), AIR_RECON_DAMAGE_FACTOR);

        return unitsDamageFactor;
    }

}
