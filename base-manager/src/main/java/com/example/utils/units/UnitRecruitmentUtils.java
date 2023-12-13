package com.example.utils.units;

import com.example.dto.UnitDTO;
import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.enums.BuildingNames;
import com.example.enums.UnitNames;
import com.example.enums.UnitsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.utils.Constants;
import com.example.utils.buildings.BuildingUtils;
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

    private final BuildingUtils buildingUtils;

    private final RestTemplate restTemplate;

    public UnitRecruitmentUtils(UnitConfigUtils unitConfigUtils, BuildingUtils buildingUtils, RestTemplate restTemplate) {
        this.unitConfigUtils = unitConfigUtils;
        this.buildingUtils = buildingUtils;
        this.restTemplate = restTemplate;
    }

    public Map<String, Integer> generateDefaultUnitsForBase() {
        Map<String, Integer> units = new HashMap<>();

        for (UnitNames unitName : UnitNames.values()) {
            units.put(unitName.getLabel(), 0);
        }

        return units;
    }

    public void validateUnitsNames(Map<String, Integer> units) {
        for (String unitName : units.keySet()) {
            if (!UnitNames.contains(unitName)) {
                throw new ResourceNotFoundException(String.format(Constants.INVALID_UNIT_NAME, unitName));
            }
        }
    }

    public void validateBuildingLevelRequirements(Base base, Map<String, Integer> units) {
        Building barracks = buildingUtils.getBuilding(base, BuildingNames.BARRACKS.getLabel());
        Building motorizedVehiclesFactory = buildingUtils.getBuilding(base, BuildingNames.MOTORIZED_VEHICLES_FACTORY.getLabel());
        Building aircraftFactory = buildingUtils.getBuilding(base, BuildingNames.AIRCRAFT_FACTORY.getLabel());

        Integer barracksLevel = barracks == null ? null : barracks.getLevel();
        Integer motorizedVehiclesFactoryLevel = motorizedVehiclesFactory == null ? null : motorizedVehiclesFactory.getLevel();
        Integer aircraftFactoryLevel = aircraftFactory == null ? null : aircraftFactory.getLevel();

        /* Check for each unit type if they are part of the unit recruitment request */

        /* Infantry */
        if (units.containsKey(UnitNames.GROUND_INFANTRY.getLabel()) && units.get(UnitNames.GROUND_INFANTRY.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_INFANTRY) {
                throw new InternalServerErrorException(String.format(Constants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.GROUND_INFANTRY.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_INFANTRY));
            }
        }
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

        /* Check if the base has the necessary resources for this (i.e. all base resources' amount are positive) */
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
