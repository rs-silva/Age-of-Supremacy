package com.example.utils.units;

import com.example.config.UnitStatusConfig;
import com.example.enums.BuildingsPropertiesNames;
import com.example.enums.UnitNames;
import com.example.enums.UnitsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UnitGenerationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UnitGenerationUtils.class);

    private final UnitConfigUtils unitConfigUtils;

    public UnitGenerationUtils(UnitConfigUtils unitConfigUtils) {
        this.unitConfigUtils = unitConfigUtils;
    }

    public Map<String, Integer> generateDefaultUnitsForBase() {
        Map<String, Integer> units = new HashMap<>();

        for (UnitNames unitName : UnitNames.values()) {
            units.put(unitName.getLabel(), 0);
        }

        return units;
    }

    public Map<String, Integer> getRequirementsToRecruitUnit(String unitName) {
        UnitStatusConfig unitStatusConfig = unitConfigUtils.getUnitConfig(unitName);
        Map<String, Integer> unitResourceConfig = unitConfigUtils.getUnitResourceConfig(unitStatusConfig);

        if (unitStatusConfig != null) {
            unitResourceConfig.put(
                    UnitsPropertiesNames.RECRUITMENT_TIME.getLabel(), unitStatusConfig.getRecruitmentTime());
            return unitResourceConfig;
        }

        LOG.info("There was an error while retrieving the recruitment information for {}.", unitName);
        throw new InternalServerErrorException(Constants.UNIT_CONFIG_NOT_FOUND_ERROR);

    }

}
