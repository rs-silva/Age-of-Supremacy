package com.example.enums;

import lombok.Getter;

@Getter
public enum BasePropertiesNames {
    DEFAULT_NAME("Default Name");

    private final String label;

    BasePropertiesNames(String label) {
        this.label = label;
    }

}

