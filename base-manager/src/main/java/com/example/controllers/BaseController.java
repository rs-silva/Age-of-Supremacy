package com.example.controllers;

import com.example.dto.BaseDTO;
import com.example.models.Base;
import com.example.services.BaseService;
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
@RequestMapping("api/base")
public class BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping("{baseId}")
    public ResponseEntity<BaseDTO> getBase(@PathVariable UUID baseId) {

        LOG.info("Retrieving base with id {}", baseId);

        BaseDTO baseDTO = baseService.getBaseInformation(baseId);

        return ResponseEntity.ok(baseDTO);
    }

    @PostMapping("{baseId}/createBuilding/{buildingType}")
    public ResponseEntity<Base> createBuildingGenerationRequest(@PathVariable UUID baseId,
                                                                @PathVariable String buildingType) {

        LOG.info("Received request to create {} in base {}", buildingType, baseId);

        baseService.createNewBuildingConstructionRequest(baseId, buildingType);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{baseId}/finishBuilding/{buildingType}")
    public ResponseEntity<Base> completeBuildingGeneration(@PathVariable UUID baseId,
                                                           @PathVariable String buildingType) {

        LOG.info("Received request to complete generation of {} in base {}", buildingType, baseId);

        baseService.completeBuildingGeneration(baseId, buildingType);

        return ResponseEntity.ok().build();
    }
}
