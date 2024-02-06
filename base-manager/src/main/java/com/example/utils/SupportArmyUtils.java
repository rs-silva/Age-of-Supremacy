package com.example.utils;

import com.example.dto.ArmySimpleDTO;
import com.example.dto.ArmyMovementEventDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import com.example.models.SupportArmy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Map;

@Component
public class SupportArmyUtils {

    private final BaseUtils baseUtils;

    private final RestTemplate restTemplate;

    public SupportArmyUtils(BaseUtils baseUtils, RestTemplate restTemplate) {
        this.baseUtils = baseUtils;
        this.restTemplate = restTemplate;
    }

    public void createSupportArmySendRequest(Base originBase, Base destinationBase, ArmySimpleDTO armySimpleDTO) {
        Map<String, Integer> armyToSend = armySimpleDTO.getUnits();

        baseUtils.removeUnitsFromBase(originBase, armyToSend);

        Timestamp arrivalTime = ArmyUtils.calculateArmyArrivalTime(originBase.getX_coordinate(), originBase.getY_coordinate(),
                destinationBase.getX_coordinate(), destinationBase.getY_coordinate(), armySimpleDTO);

        ArmyMovementEventDTO armyMovementEventDTO = ArmyMovementEventDTO.builder()
                .ownerPlayerId(originBase.getPlayer().getId())
                .ownerBaseId(originBase.getId())
                .originBaseId(originBase.getId())
                .destinationBaseId(destinationBase.getId())
                .units(armyToSend)
                .arrivalTime(arrivalTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Support Army Event to event-manager module */
        String url = "http://localhost:8083/api/event/supportArmy";
        restTemplate.postForObject(url, armyMovementEventDTO, ArmyMovementEventDTO.class);
    }

    public void createSupportArmyReturnRequest(Base ownerBase, SupportArmy supportArmy, ArmySimpleDTO armySimpleDTO) {
        Map<String, Integer> supportArmyUnits = supportArmy.getUnits();
        Map<String, Integer> armyToReturn = armySimpleDTO.getUnits();

        for (String unitName : armyToReturn.keySet()) {
            int supportArmyUnitCurrentAmount = supportArmyUnits.get(unitName);
            int unitAmountToReturn = armyToReturn.get(unitName);

            int unitUpdatedAmount = supportArmyUnitCurrentAmount - unitAmountToReturn;
            supportArmyUnits.put(unitName, unitUpdatedAmount);
        }

        /* Check if the support army had the necessary units to return (i.e. all support army units' amounts are positive) */
        for (String unitName : supportArmyUnits.keySet()) {
            int unitCurrentAmount = supportArmyUnits.get(unitName);

            if (unitCurrentAmount < 0) {
                throw new BadRequestException(BaseManagerConstants.NOT_ENOUGH_UNITS_TO_RETURN);
            }
        }

        Timestamp arrivalTime = ArmyUtils.calculateArmyArrivalTime(supportArmy.getBaseBeingSupported().getX_coordinate(), supportArmy.getBaseBeingSupported().getY_coordinate(),
                ownerBase.getX_coordinate(), ownerBase.getY_coordinate(), armySimpleDTO);

        ArmyMovementEventDTO armyMovementEventDTO = ArmyMovementEventDTO.builder()
                .ownerPlayerId(ownerBase.getPlayer().getId())
                .ownerBaseId(ownerBase.getId())
                .originBaseId(supportArmy.getBaseBeingSupported().getId())
                .destinationBaseId(ownerBase.getId())
                .units(armyToReturn)
                .arrivalTime(arrivalTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Support Army Event to event-manager module */
        String url = "http://localhost:8083/api/event/supportArmy";
        restTemplate.postForObject(url, armyMovementEventDTO, ArmyMovementEventDTO.class);
    }

}
