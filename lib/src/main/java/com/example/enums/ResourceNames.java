package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResourceNames {

    RESOURCE_1("Construction Materials"),
    RESOURCE_2("Steel"),
    RESOURCE_3("Fuel"),
    RESOURCE_4("Electronics"),
    RESOURCE_5("Ammunition");

    private final String label;

}
