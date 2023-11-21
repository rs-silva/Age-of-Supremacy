package com.example.controllers;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.services.BuildingEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event")
public class BuildingEventController {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingEventController.class);

    private final BuildingEventService buildingEventService;

    public BuildingEventController(BuildingEventService buildingEventService) {
        this.buildingEventService = buildingEventService;
    }

    @PostMapping
    public ResponseEntity<BuildingUpgradeEventDTO> registerUser(@Valid @RequestBody BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        LOG.info("Registering event = {}", buildingUpgradeEventDTO);

        buildingEventService.registerEvent(buildingUpgradeEventDTO);

        return ResponseEntity.ok().build();
    }

}
