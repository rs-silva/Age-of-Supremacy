package com.example.services;

import com.example.dto.ArmyMovementEventDTO;
import com.example.dto.ArmySimpleDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import com.example.utils.BaseManagerConstants;
import com.example.utils.BaseUtils;
import com.example.utils.units.UnitsUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.UUID;

@Service("base-manager microservice BattleService")
public class BattleService {

    private final BaseService baseService;

    private final BaseUtils baseUtils;

    private final UnitsUtils unitsUtils;

    private final RestTemplate restTemplate;

    public BattleService(BaseService baseService, BaseUtils baseUtils, UnitsUtils unitsUtils, RestTemplate restTemplate) {
        this.baseService = baseService;
        this.baseUtils = baseUtils;
        this.unitsUtils = unitsUtils;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void createAttackSendRequest(UUID originBaseId, UUID destinationBaseId, ArmySimpleDTO armySimpleDTO) {
        if (originBaseId.equals(destinationBaseId)) {
            throw new BadRequestException(BaseManagerConstants.ATTACK_ORIGIN_BASE_AND_DESTINATION_BASE_ARE_EQUAL);
        }

        Base originBase = baseService.findById(originBaseId);
        baseService.validateBaseOwnership(originBase.getPlayer().getId());

        Base destinationBase = baseService.findById(destinationBaseId);

        baseUtils.removeUnitsFromBase(originBase, armySimpleDTO.getUnits());

        Timestamp arrivalTime = unitsUtils.calculateUnitsArrivalTime(originBase, destinationBase, armySimpleDTO);

        ArmyMovementEventDTO armyMovementEventDTO = ArmyMovementEventDTO.builder()
                .ownerPlayerId(originBase.getPlayer().getId())
                .ownerBaseId(originBaseId)
                .originBaseId(originBaseId)
                .destinationBaseId(destinationBaseId)
                .units(armySimpleDTO.getUnits())
                .arrivalTime(arrivalTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Attack Army Event to event-manager module */
        String url = "http://localhost:8083/api/event/attackArmy";
        restTemplate.postForObject(url, armyMovementEventDTO, ArmyMovementEventDTO.class);
    }

}
