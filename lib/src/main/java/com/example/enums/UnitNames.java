package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UnitNames {

    GROUND_INFANTRY("Infantry"),
    GROUND_ENGINEER("Engineer"),
    GROUND_SNIPER("Sniper"),

    ARMORED_APC("Armored Personnel Carrier"),
    ARMORED_MBT("Main Battle Tank"),
    ARMORED_ARTILLERY("Artillery"),

    AIR_FIGHTER("Jet Fighter"),
    AIR_BOMBER("Bomber"),
    AIR_RECON("Recon");

    private final String label;

    public static boolean contains(String name) {

        for (UnitNames unitName : UnitNames.values()) {
            if (unitName.getLabel().equals(name)) {
                return true;
            }
        }

        return false;
    }

}
