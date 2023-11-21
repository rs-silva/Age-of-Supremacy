package com.example.controllers;

import com.example.dto.BuildingGenerationEventDTO;
import com.example.services.BuildingGenerationEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event/building/generate")
public class BuildingGenerationEventController {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingGenerationEventController.class);

    private final BuildingGenerationEventService buildingGenerationEventService;

    public BuildingGenerationEventController(BuildingGenerationEventService buildingGenerationEventService) {
        this.buildingGenerationEventService = buildingGenerationEventService;
    }

    @PostMapping
    public ResponseEntity<BuildingGenerationEventDTO> registerBuildingGenerationEvent(@Valid @RequestBody BuildingGenerationEventDTO buildingGenerationEventDTO) {
        LOG.info("Registering event = {}", buildingGenerationEventDTO);

        buildingGenerationEventService.registerEvent(buildingGenerationEventDTO);

        return ResponseEntity.ok().build();
    }

}
