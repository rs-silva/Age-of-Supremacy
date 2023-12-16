package com.example.utils;

import com.example.dto.ArmyDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Component
public class SupportArmyUtils {

    private final RestTemplate restTemplate;

    public SupportArmyUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createSupportArmyRequest(Base originBase, Base destinationBase, ArmyDTO armyDTO) {
        Map<String, Integer> baseUnits = originBase.getUnits();
        Map<String, Integer> armyToSend = armyDTO.getUnits();

        for (String unitName : armyToSend.keySet()) {
            int baseUnitCurrentAmount = baseUnits.get(unitName);
            int unitAmountToSend = armyToSend.get(unitName);

            int unitUpdatedAmount = baseUnitCurrentAmount - unitAmountToSend;
            baseUnits.put(unitName, unitUpdatedAmount);
        }

        /* Check if the base had the necessary units to send (i.e. all base units' amounts are positive) */
        for (String unitName : baseUnits.keySet()) {
            int unitCurrentAmount = baseUnits.get(unitName);

            if (unitCurrentAmount < 0) {
                throw new BadRequestException(Constants.NOT_ENOUGH_UNITS_TO_SEND);
            }
        }

        Timestamp arrivalTime = calculateArrivalTime(originBase, destinationBase);
    }

    private Timestamp calculateArrivalTime(Base originBase, Base destinationBase) {
        /* TODO calculate travelling time based on the bases' coordinates */
        int travellingTimeInSeconds = 5;

        return Timestamp.from(Instant.now().plusMillis(travellingTimeInSeconds * 1000));
    }

}
