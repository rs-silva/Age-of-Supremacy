package com.example.controllers;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.services.BuildingUpgradeEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event/building/upgrade")
public class BuildingUpgradeEventController {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingUpgradeEventController.class);

    private final BuildingUpgradeEventService buildingUpgradeEventService;

    public BuildingUpgradeEventController(BuildingUpgradeEventService buildingUpgradeEventService) {
        this.buildingUpgradeEventService = buildingUpgradeEventService;
    }

    @PostMapping
    public ResponseEntity<BuildingUpgradeEventDTO> registerBuildingUpgradeEvent(@Valid @RequestBody BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        LOG.info("Registering event = {}", buildingUpgradeEventDTO);

        buildingUpgradeEventService.registerEvent(buildingUpgradeEventDTO);

        return ResponseEntity.ok().build();
    }

}
