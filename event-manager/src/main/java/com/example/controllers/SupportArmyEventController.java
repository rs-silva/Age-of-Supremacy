package com.example.controllers;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.dto.SupportArmyEventDTO;
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

    @PostMapping("send")
    public ResponseEntity<BuildingUpgradeEventDTO> registerSupportArmyEvent(@Valid @RequestBody SupportArmyEventDTO supportArmyEventDTO) {
        LOG.info("Registering event = {}", supportArmyEventDTO);

        buildingUpgradeEventService.registerEvent(buildingUpgradeEventDTO);

        return ResponseEntity.ok().build();
    }

}
