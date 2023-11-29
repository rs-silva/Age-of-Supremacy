package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuildingsPropertiesNames {
    CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL("Amount of time in seconds to upgrade to next level"),

    RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED("Amount of resources produced per hour"),

    WAREHOUSE_CAPACITY("Amount of each resource stored"),

    DEFENSE_CENTER_OVERALL_FACTOR("Overall defense factor"),
    DEFENSE_CENTER_AA_FACTOR("Anti-air defense factor"),
    DEFENSE_CENTER_ANTITANK_FACTOR("Anti-tank defense factor"),

    DEFENSE_CENTER_OVERALL("Overall defense"),
    DEFENSE_CENTER_AA("Anti-air defense"),
    DEFENSE_CENTER_ANTITANK("Anti-tank defense");

    private final String label;

}

