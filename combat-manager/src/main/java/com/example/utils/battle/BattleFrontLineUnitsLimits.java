package com.example.utils.battle;

import com.example.enums.UnitNames;

import java.util.HashMap;
import java.util.Map;

public abstract class BattleFrontLineUnitsLimits {

    private static final int GROUND_INFANTRY_LIMIT = 300;

    private static final int GROUND_ENGINEER_LIMIT = 100;

    private static final int GROUND_SNIPER_LIMIT = 30;

    private static final int ARMORED_APC_LIMIT = 40;

    private static final int ARMORED_MBT_LIMIT = 30;

    private static final int ARMORED_ARTILLERY_LIMIT = 30;

    private static final int AIR_FIGHTER_LIMIT = 30;

    private static final int AIR_BOMBER_LIMIT = 20;

    private static final int AIR_RECON_LIMIT = 30;

    public static Map<String, Integer> getFrontLineUnitsLimits() {
        Map<String, Integer> frontLineUnitsLimits = new HashMap<>();

        frontLineUnitsLimits.put(UnitNames.GROUND_INFANTRY.getLabel(), GROUND_INFANTRY_LIMIT);
        frontLineUnitsLimits.put(UnitNames.GROUND_ENGINEER.getLabel(), GROUND_ENGINEER_LIMIT);
        frontLineUnitsLimits.put(UnitNames.GROUND_SNIPER.getLabel(), GROUND_SNIPER_LIMIT);

        frontLineUnitsLimits.put(UnitNames.ARMORED_APC.getLabel(), ARMORED_APC_LIMIT);
        frontLineUnitsLimits.put(UnitNames.ARMORED_MBT.getLabel(), ARMORED_MBT_LIMIT);
        frontLineUnitsLimits.put(UnitNames.ARMORED_ARTILLERY.getLabel(), ARMORED_ARTILLERY_LIMIT);

        frontLineUnitsLimits.put(UnitNames.AIR_FIGHTER.getLabel(), AIR_FIGHTER_LIMIT);
        frontLineUnitsLimits.put(UnitNames.AIR_BOMBER.getLabel(), AIR_BOMBER_LIMIT);
        frontLineUnitsLimits.put(UnitNames.AIR_RECON.getLabel(), AIR_RECON_LIMIT);

        return frontLineUnitsLimits;
    }

}
