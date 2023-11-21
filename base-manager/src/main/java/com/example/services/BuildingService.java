package com.example.services;

import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.repositories.BuildingRepository;
import com.example.utils.BuildingCompleteUpgradeUtils;
import com.example.utils.BuildingGenerationUtils;
import com.example.utils.Constants;
import com.example.dto.BuildingDTO;
import com.example.mappers.BuildingMapper;
import com.example.utils.BuildingRequestUpgradeUtils;
import com.example.utils.JwtAccessTokenUtils;
import com.example.utils.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    private final BuildingRequestUpgradeUtils buildingRequestUpgradeUtils;

    private final BuildingCompleteUpgradeUtils buildingCompleteUpgradeUtils;

    private final BuildingGenerationUtils buildingGenerationUtils;

    private final ResourcesUtils resourcesUtils;

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    public BuildingService(BuildingRepository buildingRepository, BuildingRequestUpgradeUtils buildingRequestUpgradeUtils, BuildingCompleteUpgradeUtils buildingCompleteUpgradeUtils, BuildingGenerationUtils buildingGenerationUtils, ResourcesUtils resourcesUtils, JwtAccessTokenUtils jwtAccessTokenUtils) {
        this.buildingRepository = buildingRepository;
        this.buildingRequestUpgradeUtils = buildingRequestUpgradeUtils;
        this.buildingCompleteUpgradeUtils = buildingCompleteUpgradeUtils;
        this.buildingGenerationUtils = buildingGenerationUtils;
        this.resourcesUtils = resourcesUtils;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
    }

    public void generateDefaultBuildingsForNewBase(Base base) {
        List<Building> buildingList = buildingGenerationUtils.generateDefaultBuildingsForNewBase();

        for (Building building : buildingList) {
            building.setBase(base);
            buildingRepository.save(building);
        }

    }

    public BuildingDTO getBuildingInformation(UUID buildingId) {
        Building building = findById(buildingId);

        validateBuildingOwnership(building.getBase().getPlayer().getId());

        boolean isBuildingMaxLevel = buildingRequestUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            return null;
        }

        Map<String, Integer> requirementsToNextLevel = buildingRequestUpgradeUtils.getRequirementsToUpgradeBuilding(building.getType(), building.getLevel());
        return BuildingMapper.buildDTO(building, requirementsToNextLevel);
    }

    @Transactional
    public void requestBuildingUpgrade(UUID buildingId) {
        Building building = findById(buildingId);

        validateBuildingOwnership(building.getBase().getPlayer().getId());

        boolean isBuildingMaxLevel = buildingRequestUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            LOG.info("Attempted to upgrade building {}, which is already at the maximum level", building.getId());
            throw new InternalServerErrorException(Constants.BUILDING_IS_ALREADY_MAX_LEVEL);
        }

        Base base = building.getBase();
        boolean areUpgradeRequirementsMet = buildingRequestUpgradeUtils.checkIfThereAreEnoughResourcesToUpgradeBuilding(base, building);

        if (!areUpgradeRequirementsMet) {
            LOG.info("Attempted to upgrade building {}, but there are no enough resources in the corresponding base.", building.getId());
            throw new InternalServerErrorException(Constants.NOT_ENOUGH_RESOURCES_TO_UPGRADE_BUILDING);
        }

        buildingRequestUpgradeUtils.requestBuildingUpgrade(base, building);
    }

    @Transactional
    public void completeUpgrade(UUID buildingId) {
        Building building = findById(buildingId);

        resourcesUtils.updateBaseResources(building.getBase());

        buildingCompleteUpgradeUtils.levelUpBuilding(building);

    }

    private void validateBuildingOwnership(UUID buildingPlayerId) {
        UUID playerIdFromToken = jwtAccessTokenUtils.retrievePlayerIdFromToken();

        if (!buildingPlayerId.equals(playerIdFromToken)) {
            LOG.error("User with id {} attempted to perform an operation in a building that belong to {}", playerIdFromToken, buildingPlayerId);
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
