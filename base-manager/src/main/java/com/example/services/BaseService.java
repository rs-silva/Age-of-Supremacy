package com.example.services;

import com.example.dto.BaseDTO;
import com.example.dto.BuildingDTO;
import com.example.dto.SupportArmyDTO;
import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.dto.ArmySimpleDTO;
import com.example.enums.BasePropertiesNames;
import com.example.exceptions.ForbiddenException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.mappers.BaseMapper;
import com.example.mappers.BuildingMapper;
import com.example.mappers.SupportArmyMapper;
import com.example.models.Base;
import com.example.models.Building;
import com.example.models.Player;
import com.example.models.SupportArmy;
import com.example.repositories.BaseRepository;
import com.example.services.buildings.BuildingUtilsService;
import com.example.utils.JwtAccessTokenUtils;
import com.example.utils.ResourcesUtils;
import com.example.interfaces.BaseSimpleView;
import com.example.utils.BaseManagerConstants;
import com.example.utils.units.UnitRecruitmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class BaseService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);

    private final BaseRepository baseRepository;

    private final BuildingService buildingService;

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    private final ResourcesUtils resourcesUtils;

    private final BuildingUtilsService buildingUtilsService;

    private final PlayerService playerService;

    private final UnitRecruitmentUtils unitRecruitmentUtils;

    public BaseService(BaseRepository baseRepository, BuildingService buildingService, JwtAccessTokenUtils jwtAccessTokenUtils, ResourcesUtils resourcesUtils, BuildingUtilsService buildingUtilsService, @Lazy PlayerService playerService, UnitRecruitmentUtils unitRecruitmentUtils) {
        this.baseRepository = baseRepository;
        this.buildingService = buildingService;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
        this.resourcesUtils = resourcesUtils;
        this.buildingUtilsService = buildingUtilsService;
        this.playerService = playerService;
        this.unitRecruitmentUtils = unitRecruitmentUtils;
    }

    public void generateBase(Player player) {
        Map<String, Double> resources = resourcesUtils.generateDefaultResourcesForBase();
        Map<String, Integer> units = unitRecruitmentUtils.generateDefaultUnitsForBase();

        Base base = Base.builder()
                .name(BasePropertiesNames.DEFAULT_NAME.getLabel())
                /* TODO Create a world manager to generate the coordinates of new bases */
                .x_coordinate(new Random().nextInt(1000))
                .y_coordinate(new Random().nextInt(1000))
                .player(player)
                .resources(resources)
                .units(units)
                .lastResourcesUpdate(Timestamp.from(Instant.now()))
                .buildings(new ArrayList<>())
                .build();

        baseRepository.save(base);
        player.addBase(base);

        LOG.info("Created base = {}", base);

        buildingService.generateDefaultBuildingsForNewBase(base);

        updateBaseAndPlayerScore(base);
    }

    @Transactional
    public BaseDTO getBaseInformation(UUID baseId) {
        Base base = findById(baseId);
        validateBaseOwnership(base.getPlayer().getId());

        List<Building> buildingList = base.getBuildings();
        List<SupportArmy> supportArmyList = base.getSupportArmies();

        List<BuildingDTO> buildingDTOList = buildingList
                .stream()
                .map(building -> {
                    Map<String, String> basicProperties = buildingUtilsService.getBasicProperties(building);
                    return BuildingMapper.buildDTO(building, basicProperties);
                })
                .toList();

        List<SupportArmyDTO> supportArmyDTOList = supportArmyList
                .stream()
                .map(supportArmy -> {
                    String ownerBaseName = getBaseName(supportArmy.getOwnerBaseId());
                    return SupportArmyMapper.buildDTO(supportArmy, ownerBaseName);
                    })
                .toList();

        return BaseMapper.buildDTO(base, buildingDTOList, supportArmyDTOList);
    }

    @Transactional
    public void createBuildingConstructionRequest(UUID baseId, String buildingType) {
        Base base = findById(baseId);

        validateBaseOwnership(base.getPlayer().getId());

        buildingService.requestBuildingGeneration(base, buildingType);
    }

    @Transactional
    public void completeBuildingConstruction(UUID baseId, String buildingType) {
        Base base = findById(baseId);

        buildingService.completeGeneration(base, buildingType);
    }

    @Transactional
    public void createUnitRecruitmentRequest(UUID baseId, ArmySimpleDTO armySimpleDTO) {
        Base base = findById(baseId);

        validateBaseOwnership(base.getPlayer().getId());
        unitRecruitmentUtils.validateUnitsNames(armySimpleDTO.getUnits());
        unitRecruitmentUtils.validateBuildingLevelRequirements(base, armySimpleDTO.getUnits());

        resourcesUtils.updateBaseResources(base);

        LOG.info("unitsRecruitmentDTO = {}", armySimpleDTO);

        unitRecruitmentUtils.createNewUnitRecruitmentRequest(base, armySimpleDTO.getUnits());
    }

    @Transactional
    public void completeUnitsRecruitment(UUID baseId, UnitsRecruitmentEventDTO unitsRecruitmentEventDTO) {
        Base base = findById(baseId);

        unitRecruitmentUtils.completeUnitsRecruitment(base, unitsRecruitmentEventDTO);
    }

    public Base findById(UUID id) {
        Optional<Base> base = baseRepository.findById(id);

        if (base.isEmpty()) {
            throw new ResourceNotFoundException(String.format(
                    BaseManagerConstants.BASE_NOT_FOUND, id));
        }

        resourcesUtils.updateBaseResources(base.get());
        return base.get();
    }

    public String getBaseName(UUID id) {
        Base base = findById(id);
        return base.getName();
    }

    public void updateBaseAndPlayerScore(Base base) {
        List<Building> buildingList = base.getBuildings();
        int baseScore = 0;

        for (Building building : buildingList) {
            baseScore += building.getScore();
        }

        base.setScore(baseScore);
        baseRepository.save(base);

        playerService.updatePlayerScore(base.getPlayer());
    }

    public void validateBaseOwnership(UUID basePlayerId) {
        UUID playerIdFromToken = jwtAccessTokenUtils.retrievePlayerIdFromToken();

        if (!basePlayerId.equals(playerIdFromToken)) {
            LOG.error("User with id {} attempted to perform an operation in a base that belong to {}", playerIdFromToken, basePlayerId);
            throw new ForbiddenException(BaseManagerConstants.BASE_DOES_NOT_BELONG_TO_THE_LOGGED_IN_PLAYER);
        }
    }

    public List<BaseSimpleView> findAllByPlayerId(UUID playerId) {
        return baseRepository.findAllByPlayerId(playerId);
    }

}
