package com.example.services;

import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.Building;
import com.example.repositories.BuildingRepository;
import com.example.utils.BuildingGenerationUtils;
import com.example.utils.Constants;
import com.example.dto.BuildingDTO;
import com.example.mappers.BuildingMapper;
import com.example.utils.BuildingUpgradeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    private final BuildingUpgradeUtils buildingUpgradeUtils;

    private final BuildingGenerationUtils buildingGenerationUtils;

    private final RestTemplate restTemplate;

    public BuildingService(BuildingRepository buildingRepository, BuildingUpgradeUtils buildingUpgradeUtils, BuildingGenerationUtils buildingGenerationUtils, RestTemplate restTemplate) {
        this.buildingRepository = buildingRepository;
        this.buildingUpgradeUtils = buildingUpgradeUtils;
        this.buildingGenerationUtils = buildingGenerationUtils;
        this.restTemplate = restTemplate;
    }

    public void generateDefaultBuildingsForNewBase(Base base) {
        List<Building> buildingList = buildingGenerationUtils.generateDefaultBuildingsForNewBase();

        for (Building building : buildingList) {
            building.setBase(base);
            buildingRepository.save(building);
        }

    }

    public BuildingDTO getBuildingInformation(Building building) {
        boolean isBuildingMaxLevel = buildingUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            return null;
        }

        Map<String, Integer> requirementsToNextLevel = buildingUpgradeUtils.getRequirementsToUpgradeBuilding(building.getType(), building.getLevel());
        return BuildingMapper.buildDTO(building, requirementsToNextLevel);
    }

    @Transactional
    public void upgradeBuilding(UUID buildingId) {
        Building building = findById(buildingId);
        /* TODO Validate if the building belongs to the player */
        boolean isBuildingMaxLevel = buildingUpgradeUtils.checkIfBuildingIsMaxLevel(building.getType(), building.getLevel());

        if (isBuildingMaxLevel) {
            LOG.info("Attempted to upgrade building {}, which is already at the maximum level", building.getId());
            throw new InternalServerErrorException(Constants.BUILDING_IS_ALREADY_MAX_LEVEL);
        }

        Base base = building.getBase();
        boolean areUpgradeRequirementsMet = buildingUpgradeUtils.checkIfThereAreEnoughResourcesToUpgradeBuilding(base, building);

        if (!areUpgradeRequirementsMet) {
            LOG.info("Attempted to upgrade building {}, but there are no enough resources in the corresponding base.", building.getId());
            throw new InternalServerErrorException(Constants.NOT_ENOUGH_RESOURCES_TO_UPGRADE_BUILDING);
        }

        buildingUpgradeUtils.upgradeBuilding(base, building);
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
