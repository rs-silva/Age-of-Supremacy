package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuildingNames {
    MAIN_BUILDING("Headquarters"),
    RESOURCE_1_FACTORY("Masonry Workshop"),
    RESOURCE_2_FACTORY("Steel Forge"),
    RESOURCE_3_FACTORY("Fuel Refinery"),
    RESOURCE_4_FACTORY("Semiconductor Factory"),
    RESOURCE_5_FACTORY("Munitions Plant"),
    WAREHOUSE("Warehouse"),
    BARRACKS("Barracks"),
    MOTORIZED_VEHICLES_FACTORY("Motorized Armory Complex"),
    AIRCRAFT_FACTORY("Aviation Command Center"),
    RESEARCH_LAB("Research Lab"),
    MARKET("Market"),
    ESPIONAGE_CENTER("Espionage Center"),
    DEFENSE_CENTER("Strategic Defense Center");

    private final String label;

}
