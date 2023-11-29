package com.example.utils.units;

import com.example.enums.UnitNames;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UnitsGenerationUtils {

    public Map<String, Integer> generateDefaultUnitsForBase() {
        Map<String, Integer> resources = new HashMap<>();

        for (UnitNames unitName : UnitNames.values()) {
            resources.put(unitName.getLabel(), 0);
        }

        return resources;
    }

}
