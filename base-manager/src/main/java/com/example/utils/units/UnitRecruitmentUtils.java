package com.example.utils.units;

import com.example.dto.UnitDTO;
import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.enums.BuildingNames;
import com.example.enums.UnitNames;
import com.example.enums.UnitsPropertiesNames;
import com.example.exceptions.BadRequestException;
import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.utils.BaseManagerConstants;
import com.example.utils.UnitConfigUtils;
import com.example.utils.buildings.BuildingsUtils;
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

    private final BuildingsUtils buildingsUtils;

    private final RestTemplate restTemplate;

    public UnitRecruitmentUtils(UnitConfigUtils unitConfigUtils, BuildingsUtils buildingsUtils, RestTemplate restTemplate) {
        this.unitConfigUtils = unitConfigUtils;
        this.buildingsUtils = buildingsUtils;
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
                throw new ResourceNotFoundException(String.format(BaseManagerConstants.INVALID_UNIT_NAME, unitName));
            }
        }
    }

    public void validateBuildingLevelRequirements(Base base, Map<String, Integer> units) {
        Building barracks = buildingsUtils.getBuilding(base, BuildingNames.BARRACKS.getLabel());
        Building motorizedVehiclesFactory = buildingsUtils.getBuilding(base, BuildingNames.MOTORIZED_VEHICLES_FACTORY.getLabel());
        Building aircraftFactory = buildingsUtils.getBuilding(base, BuildingNames.AIRCRAFT_FACTORY.getLabel());

        Integer barracksLevel = barracks == null ? null : barracks.getLevel();
        Integer motorizedVehiclesFactoryLevel = motorizedVehiclesFactory == null ? null : motorizedVehiclesFactory.getLevel();
        Integer aircraftFactoryLevel = aircraftFactory == null ? null : aircraftFactory.getLevel();

        /* Check for each unit type if they are part of the unit recruitment request */
        validateGroundUnits(units, barracksLevel);
        validateArmoredUnits(units, barracksLevel, motorizedVehiclesFactoryLevel);
        validateAirUnits(units, barracksLevel, aircraftFactoryLevel);
    }

    private void validateGroundUnits(Map<String, Integer> units, Integer barracksLevel) {
        /* Infantry */
        if (units.containsKey(UnitNames.GROUND_INFANTRY.getLabel()) && units.get(UnitNames.GROUND_INFANTRY.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_INFANTRY) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.GROUND_INFANTRY.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_INFANTRY));
            }
        }

        /* Engineer */
        if (units.containsKey(UnitNames.GROUND_ENGINEER.getLabel()) && units.get(UnitNames.GROUND_ENGINEER.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_ENGINEER) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.GROUND_ENGINEER.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_ENGINEER));
            }
        }

        /* Sniper */
        if (units.containsKey(UnitNames.GROUND_SNIPER.getLabel()) && units.get(UnitNames.GROUND_SNIPER.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_SNIPER) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.GROUND_SNIPER.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_SNIPER));
            }
        }
    }

    private void validateArmoredUnits(Map<String, Integer> units, Integer barracksLevel, Integer motorizedVehiclesFactoryLevel) {
        /* Armored Personnel Carrier */
        if (units.containsKey(UnitNames.ARMORED_APC.getLabel()) && units.get(UnitNames.ARMORED_APC.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_APC) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.ARMORED_APC.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_APC));
            }
            if (motorizedVehiclesFactoryLevel == null || motorizedVehiclesFactoryLevel < UnitsBuildingLevelRequirements.MOTORIZED_VEHICLES_FACTORY_LEVEL_FOR_APC) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.ARMORED_APC.getLabel(),
                        BuildingNames.MOTORIZED_VEHICLES_FACTORY.getLabel(),
                        UnitsBuildingLevelRequirements.MOTORIZED_VEHICLES_FACTORY_LEVEL_FOR_APC));
            }
        }

        /* Main Battle Tank */
        if (units.containsKey(UnitNames.ARMORED_MBT.getLabel()) && units.get(UnitNames.ARMORED_MBT.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_MBT) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.ARMORED_MBT.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_MBT));
            }
            if (motorizedVehiclesFactoryLevel == null || motorizedVehiclesFactoryLevel < UnitsBuildingLevelRequirements.MOTORIZED_VEHICLES_FACTORY_LEVEL_FOR_MBT) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.ARMORED_MBT.getLabel(),
                        BuildingNames.MOTORIZED_VEHICLES_FACTORY.getLabel(),
                        UnitsBuildingLevelRequirements.MOTORIZED_VEHICLES_FACTORY_LEVEL_FOR_MBT));
            }
        }

        /* Artillery */
        if (units.containsKey(UnitNames.ARMORED_ARTILLERY.getLabel()) && units.get(UnitNames.ARMORED_ARTILLERY.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_ARTILLERY) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.ARMORED_ARTILLERY.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_ARTILLERY));
            }
            if (motorizedVehiclesFactoryLevel == null || motorizedVehiclesFactoryLevel < UnitsBuildingLevelRequirements.MOTORIZED_VEHICLES_FACTORY_LEVEL_FOR_ARTILLERY) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.ARMORED_ARTILLERY.getLabel(),
                        BuildingNames.MOTORIZED_VEHICLES_FACTORY.getLabel(),
                        UnitsBuildingLevelRequirements.MOTORIZED_VEHICLES_FACTORY_LEVEL_FOR_ARTILLERY));
            }
        }
    }

    private void validateAirUnits(Map<String, Integer> units, Integer barracksLevel, Integer aircraftFactoryLevel) {
        /* Recon Plane */
        if (units.containsKey(UnitNames.AIR_RECON.getLabel()) && units.get(UnitNames.AIR_RECON.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_RECON_PLANE) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.AIR_RECON.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_RECON_PLANE));
            }
            if (aircraftFactoryLevel == null || aircraftFactoryLevel < UnitsBuildingLevelRequirements.AIRCRAFT_FACTORY_LEVEL_FOR_RECON_PLANE) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.AIR_RECON.getLabel(),
                        BuildingNames.AIRCRAFT_FACTORY.getLabel(),
                        UnitsBuildingLevelRequirements.AIRCRAFT_FACTORY_LEVEL_FOR_RECON_PLANE));
            }
        }

        /* Jet Fighter */
        if (units.containsKey(UnitNames.AIR_FIGHTER.getLabel()) && units.get(UnitNames.AIR_FIGHTER.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_JET_FIGHTER) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.AIR_FIGHTER.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_JET_FIGHTER));
            }
            if (aircraftFactoryLevel == null || aircraftFactoryLevel < UnitsBuildingLevelRequirements.AIRCRAFT_FACTORY_LEVEL_FOR_JET_FIGHTER) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.AIR_FIGHTER.getLabel(),
                        BuildingNames.AIRCRAFT_FACTORY.getLabel(),
                        UnitsBuildingLevelRequirements.AIRCRAFT_FACTORY_LEVEL_FOR_JET_FIGHTER));
            }
        }

        /* Bomber */
        if (units.containsKey(UnitNames.AIR_BOMBER.getLabel()) && units.get(UnitNames.AIR_BOMBER.getLabel()) > 0) {
            if (barracksLevel == null || barracksLevel < UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_BOMBER) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.AIR_BOMBER.getLabel(),
                        BuildingNames.BARRACKS.getLabel(),
                        UnitsBuildingLevelRequirements.BARRACKS_LEVEL_FOR_BOMBER));
            }
            if (aircraftFactoryLevel == null || aircraftFactoryLevel < UnitsBuildingLevelRequirements.AIRCRAFT_FACTORY_LEVEL_FOR_BOMBER) {
                throw new BadRequestException(String.format(BaseManagerConstants.BUILDING_LEVEL_REQUIREMENTS_NOT_MET_TO_RECRUIT_UNITS,
                        UnitNames.AIR_BOMBER.getLabel(),
                        BuildingNames.AIRCRAFT_FACTORY.getLabel(),
                        UnitsBuildingLevelRequirements.AIRCRAFT_FACTORY_LEVEL_FOR_BOMBER));
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
                throw new BadRequestException(BaseManagerConstants.NOT_ENOUGH_RESOURCES_TO_RECRUIT_UNITS);
            }
        }

        Timestamp endTime = Timestamp.from(Instant.now().plusMillis(totalRecruitmentTime * 1000L));

        UnitsRecruitmentEventDTO unitsRecruitmentEventDTO = UnitsRecruitmentEventDTO.builder()
                .baseId(base.getId())
                .units(units)
                .completionTime(endTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Unit Recruitment Event to event-manager module */
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
        throw new InternalServerErrorException(BaseManagerConstants.UNIT_CONFIG_NOT_FOUND_ERROR);

    }

}
