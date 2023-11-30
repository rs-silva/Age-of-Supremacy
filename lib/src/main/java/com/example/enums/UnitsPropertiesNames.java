package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnitsPropertiesNames {
    RECRUITMENT_TIME("Amount of time in seconds to recruit unit");

    private final String label;

}

