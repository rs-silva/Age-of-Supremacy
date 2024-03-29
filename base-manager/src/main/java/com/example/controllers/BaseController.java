package com.example.controllers;

import com.example.dto.ArmySimpleDTO;
import com.example.dto.BaseDTO;
import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BaseUnitsDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.services.BaseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> createBuildingGenerationRequest(@PathVariable UUID baseId,
                                                                @PathVariable String buildingType) {

        LOG.info("Received request to create {} in base {}", buildingType, baseId);

        baseService.createBuildingConstructionRequest(baseId, buildingType);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{baseId}/finishBuilding/{buildingType}")
    public ResponseEntity<String> completeBuildingGeneration(@PathVariable UUID baseId,
                                                           @PathVariable String buildingType) {

        LOG.info("Received request to complete generation of {} in base {}", buildingType, baseId);

        baseService.completeBuildingConstruction(baseId, buildingType);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{baseId}/recruitUnits")
    public ResponseEntity<String> createUnitRecruitmentRequest(@PathVariable UUID baseId,
                                                               @Valid @RequestBody ArmySimpleDTO armySimpleDTO) {

        LOG.info("Received request to create {} in base {}", armySimpleDTO, baseId);

        baseService.createUnitRecruitmentRequest(baseId, armySimpleDTO);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{baseId}/completeUnitsRecruitment")
    public ResponseEntity<String> completeUnitsRecruitment(@PathVariable UUID baseId,
                                                           @Valid @RequestBody UnitsRecruitmentEventDTO unitsRecruitmentEventDTO) {

        LOG.info("Received request to complete recruitment of {} in base {}", unitsRecruitmentEventDTO.getUnits(), baseId);

        baseService.completeUnitsRecruitment(baseId, unitsRecruitmentEventDTO);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{baseId}/getUnitsForNextRound")
    public ResponseEntity<BattleNewUnitsForNextRoundDTO> getBaseCurrentUnitsForNextRound(@PathVariable UUID baseId) {

        LOG.info("Received request to fetch base {} own units and support armies", baseId);

        BattleNewUnitsForNextRoundDTO battleNewUnitsForNextRoundDTO = baseService.getBaseCurrentUnitsForBattlesNextRound(baseId);

        return ResponseEntity.ok().body(battleNewUnitsForNextRoundDTO);
    }

    @GetMapping("{baseId}/getDefenseInformation")
    public ResponseEntity<BaseDefenseInformationDTO> getBaseDefenseHealthPoints(@PathVariable UUID baseId) {

        LOG.info("Received request to fetch base {} defense information", baseId);

        BaseDefenseInformationDTO baseDefenseInformation = baseService.getBaseDefenseInformation(baseId);

        LOG.info("Base defense information = {}", baseDefenseInformation);

        return ResponseEntity.ok().body(baseDefenseInformation);
    }

    @PostMapping("{baseId}/returnSupportArmiesAfterBattle")
    public ResponseEntity<String> returnSupportArmiesAfterBattle(@PathVariable UUID baseId,
                                                                 @RequestBody BaseUnitsDTO baseUnits) {

        LOG.info("Returning {} to base {}", baseUnits, baseId);

        baseService.returnSupportArmiesAfterBattle(baseId, baseUnits);

        return ResponseEntity.ok().build();
    }
}
