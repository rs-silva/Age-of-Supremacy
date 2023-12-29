package com.example.controllers;

import com.example.dto.ArmyMovementEventDTO;
import com.example.services.BattleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("combat-manager microservice")
@RequestMapping("api/attackArmy/")
public class BattleController {

    private static final Logger LOG = LoggerFactory.getLogger(BattleController.class);

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("completeSend/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<ArmyMovementEventDTO> registerAttackArmyEvent(@Valid @RequestBody ArmyMovementEventDTO armyMovementEventDTO) {
        LOG.info("Attack army {} is arriving to city {}", armyMovementEventDTO.getUnits(), armyMovementEventDTO.getDestinationBaseId());

        attackArmyEventService.registerEvent(armyMovementEventDTO);

        return ResponseEntity.ok().build();
    }


}
