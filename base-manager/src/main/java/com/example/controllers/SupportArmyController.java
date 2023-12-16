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
    public ResponseEntity<SupportArmy> createSupportArmyRequest(@PathVariable UUID originBaseId,
                                                                @PathVariable UUID destinationBaseId,
                                                                @Valid @RequestBody ArmyDTO armyDTO) {

        LOG.info("Sending {} from base {} to base {}", armyDTO, originBaseId, destinationBaseId);

        supportArmyService.createSupportArmyRequest(originBaseId, destinationBaseId, armyDTO);

        return ResponseEntity.ok().build();
    }
}
