package com.example.enums;

import com.example.utils.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum UnitNames {

    /* Ground Units */
    GROUND_INFANTRY("Infantry"),
    GROUND_ENGINEER("Engineer"),
    GROUND_SNIPER("Sniper"),

    /* Armored Units */
    ARMORED_APC("Armored Personnel Carrier"),
    ARMORED_MBT("Main Battle Tank"),
    ARMORED_ARTILLERY("Artillery"),

    /* Air Units */
    AIR_FIGHTER("Jet Fighter"),
    AIR_BOMBER("Bomber"),

    /* Non-attack units, e.g. recon planes */
    RECON("Recon");

    private final String label;

    public static boolean contains(String name) {

        for (UnitNames unitName : UnitNames.values()) {
            if (unitName.getLabel().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getAttackUnitsNames() {
        List<String> groundUnits = getGroundUnitsNames();
        List<String> armoredUnits = getArmoredUnitsNames();
        List<String> airUnits = getAirUnitsNames();

        return ListUtils.concatenateLists(groundUnits, armoredUnits, airUnits);
    }

    public static List<String> getGroundUnitsNames() {
        return getLabelsForPrefix("GROUND_");
    }

    public static List<String> getArmoredUnitsNames() {
        return getLabelsForPrefix("ARMORED_");
    }

    public static List<String> getAirUnitsNames() {
        return getLabelsForPrefix("AIR_");
    }

    private static List<String> getLabelsForPrefix(String prefix) {
        List<String> labels = new ArrayList<>();

        Arrays.stream(values())
                .filter(value -> value.name().startsWith(prefix))
                .map(UnitNames::getLabel)
                .forEach(labels::add);

        return labels;
    }

}
