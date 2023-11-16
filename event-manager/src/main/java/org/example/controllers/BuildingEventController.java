package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dto.BuildingUpgradeEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event")
public class BuildingEventController {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingEventController.class);

    @PostMapping
    public ResponseEntity<BuildingUpgradeEventDTO> registerUser(@Valid @RequestBody BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        LOG.info("Registering event = {}", buildingUpgradeEventDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new BuildingUpgradeEventDTO());
    }

}
