package com.example.utils.units;

import com.example.config.UnitConfig;
import com.example.config.UnitStatusConfig;
import org.springframework.stereotype.Component;


@Component
public class UnitStatusConfigUtils {

    private final UnitConfig unitConfig;

    public UnitStatusConfigUtils(UnitConfig unitConfig) {
        this.unitConfig = unitConfig;
    }

    public UnitStatusConfig getUnitConfig(String unitName) {
        return unitConfig.getUnits()
                .stream()
                .filter(unit -> unit.getUnitName().equals(unitName))
                .findFirst()
                .orElse(null);
    }

}
