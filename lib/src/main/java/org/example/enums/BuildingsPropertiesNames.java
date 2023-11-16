package org.example.enums;

import lombok.Getter;

@Getter
public enum BuildingsPropertiesNames {
    RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED("Amount of resources produced per hour"),
    WAREHOUSE_CAPACITY("Amount of each resource stored");

    private final String label;

    BuildingsPropertiesNames(String label) {
        this.label = label;
    }

}

