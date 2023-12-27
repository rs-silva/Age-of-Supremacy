package com.example.utils;

import com.example.dto.ArmyDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Component
public class BaseUtils {

    public void removeUnitsFromBase(Base base, Map<String, Integer> unitsToRemove) {
        Map<String, Integer> baseUnits = base.getUnits();

        for (String unitName : unitsToRemove.keySet()) {
            int baseUnitCurrentAmount = baseUnits.get(unitName);
            int unitAmountToSend = unitsToRemove.get(unitName);

            int unitUpdatedAmount = baseUnitCurrentAmount - unitAmountToSend;
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

}
