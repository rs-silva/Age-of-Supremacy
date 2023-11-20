package com.example.controllers;

import com.example.models.Base;
import com.example.models.Building;
import com.example.services.BuildingService;
import com.example.dto.BuildingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("{buildingId}")
    public ResponseEntity<BuildingDTO> getBuilding(@PathVariable UUID buildingId) {

        LOG.info("Received request to return resources needed to upgrade building with id {}", buildingId);

        Building building = buildingService.findById(buildingId);
        BuildingDTO buildingDTO = buildingService.getBuildingInformation(building);

        //Base base = baseService.findById(baseId);

        return ResponseEntity.ok(buildingDTO);
    }

    @PostMapping("/upgrade/{buildingId}")
    public ResponseEntity<Base> createBuildingUpgradeRequest(@PathVariable UUID buildingId) {

        LOG.info("Received request to upgrade building with id {}", buildingId);

        buildingService.upgradeBuilding(buildingId);

        return ResponseEntity.ok().build();
    }

}
