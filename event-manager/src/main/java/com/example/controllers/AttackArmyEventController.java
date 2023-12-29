package com.example.controllers;

import com.example.dto.ArmyMovementEventDTO;
import com.example.services.AttackArmyEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event/attackArmy")
public class AttackArmyEventController {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyEventController.class);

    private final AttackArmyEventService attackArmyEventService;

    public AttackArmyEventController(AttackArmyEventService attackArmyEventService) {
        this.attackArmyEventService = attackArmyEventService;
    }

    @PostMapping()
    public ResponseEntity<ArmyMovementEventDTO> registerAttackArmyEvent(@Valid @RequestBody ArmyMovementEventDTO armyMovementEventDTO) {
        LOG.info("Registering event = {}", armyMovementEventDTO);

        attackArmyEventService.registerEvent(armyMovementEventDTO);

        return ResponseEntity.ok().build();
    }

}
