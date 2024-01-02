package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ArmyRole {
    ATTACKING("Attacking army"),
    DEFENDING("Defending army");

    private final String label;
}
