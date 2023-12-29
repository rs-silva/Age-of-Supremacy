package com.example.controllers;

import com.example.dto.ArmyDTO;
import com.example.models.SupportArmy;
import com.example.services.CombatService;
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

@Controller
@RequestMapping("api/combat")
public class CombatController {

    private static final Logger LOG = LoggerFactory.getLogger(CombatController.class);

    private final CombatService combatService;

    public CombatController(CombatService combatService) {
        this.combatService = combatService;
    }

    @PostMapping("sendAttack/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<SupportArmy> createSupportArmySendRequest(@PathVariable UUID originBaseId,
                                                                    @PathVariable UUID destinationBaseId,
                                                                    @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("Sending attack with {} from base {} to base {}", armyDTO, originBaseId, destinationBaseId);

        combatService.createAttackSendRequest(originBaseId, destinationBaseId, armyDTO);

        return ResponseEntity.ok().build();
    }

}
