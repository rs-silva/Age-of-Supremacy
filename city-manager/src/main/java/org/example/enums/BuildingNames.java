package org.example.enums;

import lombok.Getter;

@Getter
public enum BuildingNames {
    MAIN_BUILDINGS("Headquarters"),
    BUILDING_MATERIALS_FACTORY("Masonry Workshop"),
    STEEL_FACTORY("Steel Forge"),
    FUEL_FACTORY("Fuel Refinery"),
    ELECTRONICS_FACTORY("Semiconductor Factory"),
    AMMUNITION_FACTORY("Munitions Plant"),
    WAREHOUSE("Warehouse"),
    BARRACKS("Barracks"),
    MOTORIZED_VEHICLES_FACTORY("Motorized Armory Complex"),
    AIRCRAFT_FACTORY("Aviation Command Center"),
    RESEARCH_LAB("Research Lab"),
    MARKET("Market"),
    ESPIONAGE_CENTER("Espionage Center"),
    DEFENSE_CENTER("Strategic Defense Center");

    private final String label;

    BuildingNames(String label) {
        this.label = label;
    }

}
