package com.example.controllers;

import com.example.dto.UnitDTO;
import com.example.services.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/unit")
public class UnitsController {

    private static final Logger LOG = LoggerFactory.getLogger(UnitsController.class);

    private final UnitService unitService;

    public UnitsController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping("getAllUnits")
    public ResponseEntity<List<UnitDTO>> getAllUnits() {

        LOG.info("Retrieving all units' information.");

        List<UnitDTO> unitDTOList = unitService.getAllUnitsInformation();

        return ResponseEntity.ok(unitDTOList);
    }
}
