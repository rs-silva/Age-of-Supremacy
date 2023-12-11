package com.example.services;

import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.repositories.BuildingRepository;
import com.example.services.buildings.BuildingUtilsService;
import com.example.utils.buildings.BuildingGenerationUtils;
import com.example.utils.Constants;
import com.example.dto.BuildingDTO;
import com.example.mappers.BuildingMapper;
import com.example.utils.buildings.BuildingUpgradeUtils;
import com.example.utils.JwtAccessTokenUtils;
import com.example.utils.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final BuildingUtilsService buildingUtilsService;

    private final BuildingGenerationUtils buildingGenerationUtils;

    private final ResourcesUtils resourcesUtils;

    private final BaseService baseService;

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    public BuildingService(BuildingRepository buildingRepository, BuildingUpgradeUtils buildingUpgradeUtils, BuildingUtilsService buildingUtilsService, BuildingGenerationUtils buildingGenerationUtils, ResourcesUtils resourcesUtils, @Lazy BaseService baseService, JwtAccessTokenUtils jwtAccessTokenUtils) {
        this.buildingRepository = buildingRepository;
        this.buildingUpgradeUtils = buildingUpgradeUtils;
        this.buildingUtilsService = buildingUtilsService;
        this.buildingGenerationUtils = buildingGenerationUtils;
        this.resourcesUtils = resourcesUtils;
        this.baseService = baseService;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
    }

    public void generateDefaultBuildingsForNewBase(Base base) {
        List<Building> buildingList = buildingGenerationUtils.generateDefaultBuildingsForNewBase();

        for (Building building : buildingList) {
            building.setBase(base);
            buildingRepository.save(building);
            base.addBuilding(building);
        }

    }

    public BuildingDTO getBuildingInformation(UUID buildingId) {
        Building building = findById(buildingId);

        validateBuildingOwnership(building.getBase().getPlayer().getId());

        boolean isBuildingMaxLevel = buildingUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        Map<String, Integer> requirementsToNextLevel = new HashMap<>();
        if (!isBuildingMaxLevel) {
            requirementsToNextLevel = buildingUpgradeUtils.getRequirementsToUpgradeBuilding(building.getType(), building.getLevel());
        }

        Map<String, String> basicProperties = buildingUtilsService.getBasicProperties(building);
        Map<String, String> additionalProperties = buildingUtilsService.getAdditionalProperties(building);

        return BuildingMapper.buildDTO(building, basicProperties, additionalProperties, requirementsToNextLevel);
    }

    public void requestBuildingGeneration(Base base, String buildingType) {
        boolean doesBuildingAlreadyExist = buildingGenerationUtils.checkIfBuildingAlreadyExists(base, buildingType);

        if (doesBuildingAlreadyExist) {
            LOG.error("Attempted to create building {} in base {}, but this building already exists in this base.", buildingType, base.getId());
            throw new InternalServerErrorException(Constants.BUILDING_ALREADY_EXISTS);
        }

        resourcesUtils.updateBaseResources(base);

        boolean areUpgradeRequirementsMet = buildingUpgradeUtils.checkIfThereAreEnoughResourcesToUpgradeBuilding(base, buildingType, 1);

        if (!areUpgradeRequirementsMet) {
            LOG.error("Attempted to create building {} in base {}, but there are not enough resources.", buildingType, base.getId());
            throw new InternalServerErrorException(Constants.NOT_ENOUGH_RESOURCES_TO_UPGRADE_BUILDING);
        }

        buildingGenerationUtils.requestBuildingGeneration(base, buildingType);
    }

    @Transactional
    public void requestBuildingUpgrade(UUID buildingId) {
        Building building = findById(buildingId);

        validateBuildingOwnership(building.getBase().getPlayer().getId());

        boolean isBuildingMaxLevel = buildingUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            LOG.error("Attempted to upgrade building {}, which is already at the maximum level", building.getId());
            throw new InternalServerErrorException(Constants.BUILDING_IS_ALREADY_MAX_LEVEL);
        }

        Base base = building.getBase();
        resourcesUtils.updateBaseResources(base);

        boolean areUpgradeRequirementsMet = buildingUpgradeUtils.checkIfThereAreEnoughResourcesToUpgradeBuilding(base, building.getType(), building.getLevel());

        if (!areUpgradeRequirementsMet) {
            LOG.error("Attempted to upgrade building {}, but there are no enough resources in the corresponding base.", building.getId());
            throw new InternalServerErrorException(Constants.NOT_ENOUGH_RESOURCES_TO_UPGRADE_BUILDING);
        }

        buildingUpgradeUtils.requestBuildingUpgrade(base, building);
    }

    @Transactional
    public void completeGeneration(Base base, String buildingType) {
        resourcesUtils.updateBaseResources(base);

        Building building = buildingGenerationUtils.completeBuildingGeneration(base, buildingType);
        building.setBase(base);

        buildingRepository.save(building);

        base.addBuilding(building);
        baseService.updateBaseAndPlayerScore(base);
    }

    @Transactional
    public void completeUpgrade(UUID buildingId) {
        Building building = findById(buildingId);

        resourcesUtils.updateBaseResources(building.getBase());

        String buildingType = building.getType();
        int buildingCurrentLevel = building.getLevel();

        building.setLevel(buildingCurrentLevel + 1);

        int score = buildingUpgradeUtils.getBuildingScoreForSpecificLevel(buildingType, building.getLevel());
        building.setScore(score);

        baseService.updateBaseAndPlayerScore(building.getBase());
    }

    private void validateBuildingOwnership(UUID buildingPlayerId) {
        UUID playerIdFromToken = jwtAccessTokenUtils.retrievePlayerIdFromToken();

        if (!buildingPlayerId.equals(playerIdFromToken)) {
            LOG.error("User with id {} attempted to perform an operation in a building that belong to player with id {}", playerIdFromToken, buildingPlayerId);
            throw new InternalServerErrorException(Constants.BUILDING_DOES_NOT_BELONG_TO_THE_LOGGED_IN_PLAYER);
        }
    }

    public Building findById(UUID id) {
        Optional<Building> building = buildingRepository.findById(id);

        if (building.isEmpty()) {
            throw new ResourceNotFoundException(String.format(
                    Constants.BUILDING_NOT_FOUND, id));
        }

        return building.get();
    }

}
