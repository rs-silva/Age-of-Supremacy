package com.example.utils;

import com.example.dto.BaseDefenseInformationDTO;
import com.example.enums.BuildingNames;
import com.example.enums.BuildingsPropertiesNames;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.services.buildings.BuildingInterfaceService;
import com.example.utils.buildings.BuildingsUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BaseUtils {

    private final BuildingsUtils buildingsUtils;

    private final BuildingInterfaceService buildingInterfaceService;

    public BaseUtils(BuildingsUtils buildingsUtils, BuildingInterfaceService buildingInterfaceService) {
        this.buildingsUtils = buildingsUtils;
        this.buildingInterfaceService = buildingInterfaceService;
    }

    public void removeUnitsFromBase(Base base, Map<String, Integer> unitsToRemove) {
        Map<String, Integer> baseUnits = base.getUnits();

        for (String unitName : unitsToRemove.keySet()) {
            int baseUnitCurrentAmount = baseUnits.get(unitName);
            int unitAmountToRemove = unitsToRemove.get(unitName);

            int unitUpdatedAmount = baseUnitCurrentAmount - unitAmountToRemove;
            baseUnits.put(unitName, unitUpdatedAmount);
        }

        /* Check if the base had the necessary units to remove (i.e. all base units' amounts are positive) */
        for (String unitName : baseUnits.keySet()) {
            int unitCurrentAmount = baseUnits.get(unitName);

            if (unitCurrentAmount < 0) {
                throw new BadRequestException(BaseManagerConstants.NOT_ENOUGH_UNITS_IN_THE_BASE);
            }
        }
    }

    public BaseDefenseInformationDTO getBaseDefenseInformation(Base base) {
        Building strategicDefenseCenter = buildingsUtils.getBuilding(base, BuildingNames.DEFENSE_CENTER.getLabel());

        if (strategicDefenseCenter == null) {
            return new BaseDefenseInformationDTO(0, 0, 0, 0);
        }

        Map<String, String> strategicDefenseCenterProperties = buildingInterfaceService.getAdditionalProperties(strategicDefenseCenter);
        int groundDefense = Integer.parseInt(strategicDefenseCenterProperties.get(BuildingsPropertiesNames.DEFENSE_CENTER_GROUND_POWER.getLabel()));
        int antiTankDefense = Integer.parseInt(strategicDefenseCenterProperties.get(BuildingsPropertiesNames.DEFENSE_CENTER_ANTITANK_POWER.getLabel()));
        int antiAirDefense = Integer.parseInt(strategicDefenseCenterProperties.get(BuildingsPropertiesNames.DEFENSE_CENTER_AA_POWER.getLabel()));
        int defenseHealthPoints = Integer.parseInt(strategicDefenseCenterProperties.get(BuildingsPropertiesNames.DEFENSE_CENTER_HEALTH_POINTS.getLabel()));

        return BaseDefenseInformationDTO.builder()
                .groundDefensePower(groundDefense)
                .antiTankDefensePower(antiTankDefense)
                .antiAirDefensePower(antiAirDefense)
                .defenseHealthPoints(defenseHealthPoints)
                .build();
    }

}
