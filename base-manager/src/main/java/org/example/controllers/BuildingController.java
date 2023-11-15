package org.example.controllers;

import org.example.models.Base;
import org.example.services.BuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/api/building")
public class BuildingController {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingController.class);

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping("/upgradeRequest/{buildingId}")
    public ResponseEntity<Base> createBuildingUpgradeRequest(@PathVariable UUID buildingId) {

        //LOG.info("Retrieving base with id {}", baseId);

        buildingService.upgradeBuilding(buildingId);

        //Base base = baseService.findById(baseId);

        return ResponseEntity.ok(new Base());
    }

}
