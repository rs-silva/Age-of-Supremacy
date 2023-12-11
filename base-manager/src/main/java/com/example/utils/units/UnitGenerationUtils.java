package com.example.utils.units;

import com.example.dto.UnitDTO;
import com.example.dto.UnitsRecruitmentRequestDTO;
import com.example.enums.BuildingsPropertiesNames;
import com.example.enums.UnitNames;
import com.example.enums.UnitsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
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

    public void validateUnitsNames(Map<String, Integer> units) {
        for (String unitName : units.keySet()) {
            if (!UnitNames.contains(unitName)) {
                LOG.error(String.format(Constants.INVALID_UNIT_NAME, unitName));
                throw new ResourceNotFoundException(String.format(Constants.INVALID_UNIT_NAME, unitName));
            }
        }
    }

    public void createNewUnitRecruitmentRequest(Base base, UnitsRecruitmentRequestDTO unitsRecruitmentRequestDTO) {

    }

    public Map<String, Integer> generateDefaultUnitsForBase() {
        Map<String, Integer> units = new HashMap<>();

        for (UnitNames unitName : UnitNames.values()) {
            units.put(unitName.getLabel(), 0);
        }

        return units;
    }

    public boolean checkIfThereAreEnoughResourcesToRecruitUnits(Base base, Map<String, Integer> units) {
        Map<String, Double> baseResources = base.getResources();

        for (String unitName : units.keySet()) {
            int unitQuantity = units.get(unitName);
            Map<String, Integer> resourcesRequired = getRequirementsToRecruitUnit(unitName);

            /* Remove time requirement which is not needed for this (only resources) */
            resourcesRequired.remove(BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel());

        }



        for (String resourceName : resourcesRequired.keySet()) {
            if (baseResources.containsKey(resourceName)) {
                Double currentResourceAmount = baseResources.get(resourceName);
                Integer resourceAmountRequired = resourcesRequired.get(resourceName);
                if (currentResourceAmount < resourceAmountRequired) {
                    return false;
                }
            }
            else {
                LOG.error("There was an error while upgrading a building.\n" +
                        "The base {} does not contain information about resource {}", base.getId(), resourceName);
                throw new InternalServerErrorException(Constants.BASE_NO_INFORMATION_ABOUT_RESOURCE_AMOUNT);
            }
        }

        return true;
    }

    public Map<String, Integer> getRequirementsToRecruitUnit(String unitName) {
        UnitDTO unitDTO = unitConfigUtils.getUnitConfig(unitName);
        Map<String, Integer> unitResourceConfig = unitConfigUtils.getUnitResourceConfig(unitDTO);

        if (unitResourceConfig != null) {
            unitResourceConfig.put(
                    UnitsPropertiesNames.RECRUITMENT_TIME.getLabel(), unitDTO.getRecruitmentTime());
            return unitResourceConfig;
        }

        LOG.error("There was an error while retrieving the recruitment information for {}.", unitName);
        throw new InternalServerErrorException(Constants.UNIT_CONFIG_NOT_FOUND_ERROR);

    }

}
