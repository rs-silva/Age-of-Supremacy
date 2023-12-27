package com.example.utils;

import com.example.dto.ArmyDTO;
import com.example.dto.SupportArmyEventDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import com.example.models.SupportArmy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Component
public class SupportArmyUtils {

    private final BaseUtils baseUtils;

    private final RestTemplate restTemplate;

    public SupportArmyUtils(BaseUtils baseUtils, RestTemplate restTemplate) {
        this.baseUtils = baseUtils;
        this.restTemplate = restTemplate;
    }

    public void createSupportArmySendRequest(Base originBase, Base destinationBase, ArmyDTO armyDTO) {
        Map<String, Integer> armyToSend = armyDTO.getUnits();

        baseUtils.removeUnitsFromBase(originBase, armyToSend);

        Timestamp arrivalTime = calculateArrivalTime(originBase, destinationBase, armyDTO);

        SupportArmyEventDTO supportArmyEventDTO = SupportArmyEventDTO.builder()
                .ownerBaseId(originBase.getId())
                .originBaseId(originBase.getId())
                .destinationBaseId(destinationBase.getId())
                .units(armyToSend)
                .arrivalTime(arrivalTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Support Army Event to event-manager module */
        String url = "http://localhost:8083/api/event/supportArmy";
        restTemplate.postForObject(url, supportArmyEventDTO, SupportArmyEventDTO.class);
    }

    public void createSupportArmyReturnRequest(Base ownerBase, SupportArmy supportArmy, ArmyDTO armyDTO) {
        Map<String, Integer> supportArmyUnits = supportArmy.getUnits();
        Map<String, Integer> armyToReturn = armyDTO.getUnits();

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

        Timestamp arrivalTime = calculateArrivalTime(supportArmy.getBaseBeingSupported(), ownerBase, armyDTO);

        SupportArmyEventDTO supportArmyEventDTO = SupportArmyEventDTO.builder()
                .ownerBaseId(ownerBase.getId())
                .originBaseId(supportArmy.getBaseBeingSupported().getId())
                .destinationBaseId(ownerBase.getId())
                .units(armyToReturn)
                .arrivalTime(arrivalTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Support Army Event to event-manager module */
        String url = "http://localhost:8083/api/event/supportArmy";
        restTemplate.postForObject(url, supportArmyEventDTO, SupportArmyEventDTO.class);
    }

    private Timestamp calculateArrivalTime(Base originBase, Base destinationBase, ArmyDTO armyDTO) {
        /* TODO calculate travelling time based on the bases' coordinates and the units' movement speed */
        int travellingTimeInSeconds = 5;

        return Timestamp.from(Instant.now().plusMillis(travellingTimeInSeconds * 1000));
    }

}
