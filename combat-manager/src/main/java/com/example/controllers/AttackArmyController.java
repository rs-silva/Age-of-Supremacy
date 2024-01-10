package com.example.controllers;

import com.example.dto.ArmySimpleDTO;
import com.example.dto.ArmyMovementEventDTO;
import com.example.services.AttackArmyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller("combat-manager microservice AttackArmyController")
@RequestMapping("api/attackArmy/")
public class AttackArmyController {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyController.class);

    private final AttackArmyService attackArmyService;

    public AttackArmyController(AttackArmyService attackArmyService) {
        this.attackArmyService = attackArmyService;
    }

    @PostMapping("completeSend/player/{ownerPlayerId}/from/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<ArmyMovementEventDTO> completeAttackArmySendRequest(@PathVariable UUID ownerPlayerId,
                                                                              @PathVariable UUID originBaseId,
                                                                              @PathVariable UUID destinationBaseId,
                                                                              @Valid @RequestBody ArmySimpleDTO armySimpleDTO) {

        LOG.info("Attack army {} is arriving to city {}", armySimpleDTO.getUnits(), destinationBaseId);

        attackArmyService.addAttackArmy(ownerPlayerId, originBaseId, destinationBaseId, armySimpleDTO);

        return ResponseEntity.ok().build();
    }

}
