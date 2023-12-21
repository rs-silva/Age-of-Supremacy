package com.example.controllers;

import com.example.dto.ArmyDTO;
import com.example.models.SupportArmy;
import com.example.services.SupportArmyService;
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
@RequestMapping("api/supportArmy")
public class SupportArmyController {

    private static final Logger LOG = LoggerFactory.getLogger(SupportArmyController.class);

    private final SupportArmyService supportArmyService;

    public SupportArmyController(SupportArmyService supportArmyService) {
        this.supportArmyService = supportArmyService;
    }

    @PostMapping("send/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<SupportArmy> createSupportArmySendRequest(@PathVariable UUID originBaseId,
                                                                    @PathVariable UUID destinationBaseId,
                                                                    @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("Sending {} from base {} to base {}", armyDTO, originBaseId, destinationBaseId);

        supportArmyService.createSupportArmySendRequest(originBaseId, destinationBaseId, armyDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("completeSend/{originBaseId}/to/{destinationBaseId}")
    public ResponseEntity<SupportArmy> completeSupportArmySendRequest(@PathVariable UUID originBaseId,
                                                                      @PathVariable UUID destinationBaseId,
                                                                      @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("{} are arriving to base {}", armyDTO, destinationBaseId);

        supportArmyService.completeSupportArmySendRequest(originBaseId, destinationBaseId, armyDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("return/{supportArmyId}")
    public ResponseEntity<SupportArmy> createSupportArmyReturnRequest(@PathVariable UUID supportArmyId,
                                                                      @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("Returning {} from support army with id {}", armyDTO, supportArmyId);

        supportArmyService.createSupportArmyReturnRequest(supportArmyId, armyDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("completeReturn/{ownerBaseId}")
    public ResponseEntity<SupportArmy> completeSupportArmyReturnRequest(@PathVariable UUID ownerBaseId,
                                                                        @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("{} are returning to owner base {}.", armyDTO, ownerBaseId);

        supportArmyService.completeSupportArmyReturnRequest(ownerBaseId, armyDTO);

        return ResponseEntity.ok().build();
    }
}
