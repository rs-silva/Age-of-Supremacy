package com.example.utils.units;

import com.example.dto.UnitDTO;
import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.enums.UnitNames;
import com.example.enums.UnitsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class UnitRecruitmentUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UnitRecruitmentUtils.class);

    private final UnitConfigUtils unitConfigUtils;

    private final RestTemplate restTemplate;

    public UnitRecruitmentUtils(UnitConfigUtils unitConfigUtils, RestTemplate restTemplate) {
        this.unitConfigUtils = unitConfigUtils;
        this.restTemplate = restTemplate;
    }

    public void validateUnitsNames(Map<String, Integer> units) {
        for (String unitName : units.keySet()) {
            if (!UnitNames.contains(unitName)) {
                throw new ResourceNotFoundException(String.format(Constants.INVALID_UNIT_NAME, unitName));
            }
        }
    }

    public Map<String, Integer> generateDefaultUnitsForBase() {
        Map<String, Integer> units = new HashMap<>();

        for (UnitNames unitName : UnitNames.values()) {
            units.put(unitName.getLabel(), 0);
        }

        return units;
    }

    public void createNewUnitRecruitmentRequest(Base base, Map<String, Integer> units) {
        Map<String, Double> baseResources = base.getResources();
        int totalRecruitmentTime = 0;

        for (String unitName : units.keySet()) {
            int unitQuantity = units.get(unitName);
            Map<String, Integer> resourcesRequired = getRequirementsToRecruitUnit(unitName);

            int recruitmentTimePerUnit = resourcesRequired.remove(UnitsPropertiesNames.RECRUITMENT_TIME.getLabel());
            totalRecruitmentTime += recruitmentTimePerUnit * unitQuantity;

            for (String resourceName : resourcesRequired.keySet()) {
                int resourceAmountRequiredPerUnit = resourcesRequired.get(resourceName);
                int totalResourceAmountRequired = resourceAmountRequiredPerUnit * unitQuantity;

                double baseCurrentResourceAmount = baseResources.get(resourceName);
                double baseUpdatedResourceAmount = baseCurrentResourceAmount - totalResourceAmountRequired;

                baseResources.put(resourceName, baseUpdatedResourceAmount);
            }
        }

        /* Check if the base had necessary resources for this (i.e. all base resources' amount are positive) */
        for (String resourceName : baseResources.keySet()) {
            double resourceAmount = baseResources.get(resourceName);

            if (resourceAmount < 0) {
                throw new InternalServerErrorException(Constants.NOT_ENOUGH_RESOURCES_TO_RECRUIT_UNITS);
            }
        }

        Timestamp endTime = Timestamp.from(Instant.now().plusMillis(totalRecruitmentTime * 1000L));

        UnitsRecruitmentEventDTO unitsRecruitmentEventDTO = UnitsRecruitmentEventDTO.builder()
                .baseId(base.getId())
                .units(units)
                .completionTime(endTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Building Generation Event to event-manager module */
        String url = "http://localhost:8083/api/event/units/recruit";
        restTemplate.postForObject(url, unitsRecruitmentEventDTO, UnitsRecruitmentEventDTO.class);
    }

    public void completeUnitsRecruitment(Base base, UnitsRecruitmentEventDTO unitsRecruitmentEventDTO) {
        Map<String, Integer> baseCurrentUnits = base.getUnits();
        Map<String, Integer> newUnits = unitsRecruitmentEventDTO.getUnits();

        for (String unitName : newUnits.keySet()) {
            int unitCurrentAmount = baseCurrentUnits.get(unitName);
            int unitsToAdd = newUnits.get(unitName);

            int unitTotalAmount = unitCurrentAmount + unitsToAdd;

            baseCurrentUnits.put(unitName, unitTotalAmount);
        }
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
