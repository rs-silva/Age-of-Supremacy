package com.example.controllers;

import com.example.dto.ArmySimpleDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.models.SupportArmy;
import com.example.services.BattleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller("base-manager microservice BattleController")
@RequestMapping("api/battle")
public class BattleController {

    private static final Logger LOG = LoggerFactory.getLogger(BattleController.class);

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("sendAttack/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<SupportArmy> createSupportArmySendRequest(@PathVariable UUID originBaseId,
                                                                    @PathVariable UUID destinationBaseId,
                                                                    @Valid @RequestBody ArmySimpleDTO armySimpleDTO) {

        LOG.info("Sending attack with {} from base {} to base {}", armySimpleDTO, originBaseId, destinationBaseId);

        battleService.createAttackSendRequest(originBaseId, destinationBaseId, armySimpleDTO);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{baseId}/getUnitsForNextRound")
    public ResponseEntity<BattleNewUnitsForNextRoundDTO> getBaseCurrentUnitsForNextRound(@PathVariable UUID baseId) {

        LOG.info("Received request to fetch base {} own units and support armies", baseId);

        BattleNewUnitsForNextRoundDTO battleNewUnitsForNextRoundDTO = battleService.getBaseCurrentUnitsForBattlesNextRound(baseId);

        LOG.info("New Units = {}", battleNewUnitsForNextRoundDTO);

        return ResponseEntity.ok().body(battleNewUnitsForNextRoundDTO);
    }
}
