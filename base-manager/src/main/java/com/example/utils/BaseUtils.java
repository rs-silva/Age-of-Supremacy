package com.example.utils;

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

    public Integer getBaseDefenseHealthPoints(Base base) {
        Building strategicDefenseCenter = buildingsUtils.getBuilding(base, BuildingNames.DEFENSE_CENTER.getLabel());

        if (strategicDefenseCenter == null) {
            return 0;
        }

        Map<String, String> strategicDefenseCenterProperties = buildingInterfaceService.getAdditionalProperties(strategicDefenseCenter);
        return Integer.valueOf(strategicDefenseCenterProperties.get(BuildingsPropertiesNames.DEFENSE_CENTER_OVERALL.getLabel()));
    }

}
