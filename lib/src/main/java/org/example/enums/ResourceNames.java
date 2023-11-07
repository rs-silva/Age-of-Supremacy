package org.example.enums;

import lombok.Getter;

@Getter
public enum ResourceNames {
    RESOURCE_1("Construction Materials"),
    RESOURCE_2("Steel"),
    RESOURCE_3("Fuel"),
    RESOURCE_4("Electronics"),
    RESOURCE_5("Ammunition");

    private final String label;

    ResourceNames(String label) {
        this.label = label;
    }
}
