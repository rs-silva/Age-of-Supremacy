package com.example.controllers;

import com.example.dto.ArmyDTO;
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

@Controller("combat-manager microservice")
@RequestMapping("api/attackArmy/")
public class AttackArmyController {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyController.class);

    private final AttackArmyService attackArmyService;

    public AttackArmyController(AttackArmyService attackArmyService) {
        this.attackArmyService = attackArmyService;
    }

    @PostMapping("completeSend/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<ArmyMovementEventDTO> completeAttackArmySendRequest(@PathVariable UUID originBaseId,
                                                                              @PathVariable UUID destinationBaseId,
                                                                              @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("Attack army {} is arriving to city {}", armyDTO.getUnits(), destinationBaseId);

        attackArmyService.addAttackArmy(originBaseId, destinationBaseId, armyDTO);

        return ResponseEntity.ok().build();
    }

}
