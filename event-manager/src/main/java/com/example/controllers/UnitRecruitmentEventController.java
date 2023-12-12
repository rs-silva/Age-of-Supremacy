package com.example.controllers;

import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.services.UnitRecruitmentEventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event/units/recruit")
public class UnitRecruitmentEventController {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingUpgradeEventController.class);

    private final UnitRecruitmentEventService unitRecruitmentEventService;

    public UnitRecruitmentEventController(UnitRecruitmentEventService unitRecruitmentEventService) {
        this.unitRecruitmentEventService = unitRecruitmentEventService;
    }

    @PostMapping
    public ResponseEntity<UnitsRecruitmentEventDTO> registerUnitRecruitmentEvent(@Valid @RequestBody UnitsRecruitmentEventDTO unitsRecruitmentEventDTO) {
        LOG.info("Registering event = {}", unitsRecruitmentEventDTO);

        unitRecruitmentEventService.registerEvent(unitsRecruitmentEventDTO);

        return ResponseEntity.ok().build();
    }

}
