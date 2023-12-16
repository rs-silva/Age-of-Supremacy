package com.example.utils.units;

import com.example.config.UnitConfig;
import com.example.dto.UnitDTO;
import com.example.enums.ResourceNames;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UnitConfigUtils {

    private final UnitConfig unitConfig;

    public UnitConfigUtils(UnitConfig unitConfig) {
        this.unitConfig = unitConfig;
    }

    public UnitDTO getUnitConfig(String unitName) {
        return unitConfig.getUnits()
                .stream()
                .filter(unit -> unit.getUnitName().equals(unitName))
                .findFirst()
                .orElse(null);
    }

    public Map<String, Integer> getUnitResourceConfig(UnitDTO unitDTO) {
        if (unitDTO != null) {
            Map<String, Integer> resources = new HashMap<>();

            for (ResourceNames resourceName : ResourceNames.values()) {
                unitDTO.getResourceRequirements()
                        .stream()
                        .filter(resource -> resource.getResourceName().equals(resourceName.getLabel()))
                        .findFirst()
                        .ifPresent(unitResourceConfig -> resources.put(unitResourceConfig.getResourceName(), unitResourceConfig.getQuantity()));
            }

            return resources;
        }

        return null;
    }

}
