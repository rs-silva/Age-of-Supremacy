package com.example.controllers;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.dto.SupportArmyEventDTO;
import com.example.services.SupportArmyEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event/supportArmy")
public class SupportArmyEventController {

    private static final Logger LOG = LoggerFactory.getLogger(SupportArmyEventController.class);

    private final SupportArmyEventService supportArmyEventService;

    public SupportArmyEventController(SupportArmyEventService supportArmyEventService) {
        this.supportArmyEventService = supportArmyEventService;
    }

    @PostMapping()
    public ResponseEntity<BuildingUpgradeEventDTO> registerSupportArmyEvent(@Valid @RequestBody SupportArmyEventDTO supportArmyEventDTO) {
        LOG.info("Registering event = {}", supportArmyEventDTO);

        supportArmyEventService.registerEvent(supportArmyEventDTO);

        return ResponseEntity.ok().build();
    }

}
