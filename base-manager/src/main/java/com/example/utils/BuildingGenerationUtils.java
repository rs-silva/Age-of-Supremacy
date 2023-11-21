package com.example.utils;

import com.example.dto.BuildingGenerationEventDTO;
import com.example.enums.BuildingNames;
import com.example.enums.BuildingsPropertiesNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.models.Base;
import com.example.models.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BuildingGenerationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingGenerationUtils.class);

    private final ResourcesUtils resourcesUtils;

    private final BuildingRequestUpgradeUtils buildingRequestUpgradeUtils;

    private final RestTemplate restTemplate;

    public BuildingGenerationUtils(ResourcesUtils resourcesUtils, BuildingRequestUpgradeUtils buildingRequestUpgradeUtils, RestTemplate restTemplate) {
        this.resourcesUtils = resourcesUtils;
        this.buildingRequestUpgradeUtils = buildingRequestUpgradeUtils;
        this.restTemplate = restTemplate;
    }

    public List<Building> generateDefaultBuildingsForNewBase() {
        List<Building> buildingList = new ArrayList<>();

        /* Resource Production Buildings */
        Building resource1Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_1_FACTORY.getLabel());
        buildingList.add(resource1Producer);

        Building resource2Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_2_FACTORY.getLabel());
        buildingList.add(resource2Producer);

        Building resource3Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_3_FACTORY.getLabel());
        buildingList.add(resource3Producer);

        Building resource4Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_4_FACTORY.getLabel());
        buildingList.add(resource4Producer);

        Building resource5Producer = generateResourceProductionBuilding(BuildingNames.RESOURCE_5_FACTORY.getLabel());
        buildingList.add(resource5Producer);

        /* Other Buildings */
        Building warehouse = generateWarehouse();
        buildingList.add(warehouse);

        Building mainBuilding = generateMainBuilding();
        buildingList.add(mainBuilding);

        return buildingList;
    }

    public boolean checkIfBuildingAlreadyExists(Base base, String buildingType) {
        List<Building> buildingList = base.getBuildings();

        for (Building building : buildingList) {
            if (building.getType().equals(buildingType)) {
                return true;
            }
        }

        return false;
    }

    public void requestBuildingGeneration(Base base, String buildingType) {
        Map<String, Double> baseResources = base.getResources();
        Map<String, Integer> resourcesRequired = getRequirementsToGenerateBuilding(buildingType);

        /* Remove time requirement which is not needed for this (only resources) */
        Integer constructionTime = resourcesRequired.remove(BuildingsPropertiesNames.CONSTRUCTION_TIME_TO_UPGRADE_TO_NEXT_LEVEL.getLabel());

        for (String resourceName : resourcesRequired.keySet()) {
            Double currentResourceAmount = baseResources.get(resourceName);
            Integer resourceAmountRequired = resourcesRequired.get(resourceName);

            baseResources.put(resourceName, currentResourceAmount - resourceAmountRequired);
        }

        Timestamp endTime = Timestamp.from(Instant.now().plusMillis(constructionTime * 1000));

        BuildingGenerationEventDTO buildingGenerationEventDTO = BuildingGenerationEventDTO.builder()
                .baseId(base.getId())
                .buildingType(buildingType)
                .completionTime(endTime)
                .build();

        /* TODO Remove hardcoded url */
        /* Send Building Generation Event to event-manager module */
        String url = "http://localhost:8083/api/event/building/generate";
        restTemplate.postForObject(url, buildingGenerationEventDTO, BuildingGenerationEventDTO.class);
    }

    private Map<String, Integer> getRequirementsToGenerateBuilding(String buildingType) {
        return buildingRequestUpgradeUtils.getRequirementsToUpgradeBuilding(buildingType, 0);
    }

    public Building completeBuildingGeneration(Base base, String buildingType) {
        boolean doesBuildingAlreadyExist = checkIfBuildingAlreadyExists(base, buildingType);

        if (doesBuildingAlreadyExist) {
            LOG.info("Attempted to create building {} in base {}, but this building already exists in this base.", buildingType, base.getId());
            throw new InternalServerErrorException(Constants.BUILDING_ALREADY_EXISTS);
        }

        Building newBuilding = null;

        if (buildingType.equals(BuildingNames.DEFENSE_CENTER.getLabel())) {
            newBuilding = generateDefenseCenter();
        }

        if (newBuilding == null) {
            LOG.info("Attempted to finish generation of building {} in base {}, but the building's name is invalid.", buildingType, base.getId());
            throw new InternalServerErrorException(Constants.INVALID_BUILDING_NAME);
        }

        return newBuilding;
    }


    public Building generateResourceProductionBuilding(String type) {
        Map<String, String> properties = new HashMap<>();
        Double amountOfResourcesProduced = resourcesUtils.getAmountOfResourcesProducedForLevel(1);
        properties.put(BuildingsPropertiesNames.RESOURCE_FACTORY_AMOUNT_OF_RESOURCES_PRODUCED.getLabel(), amountOfResourcesProduced.toString());

        return Building.builder()
                .type(type)
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

    public Building generateWarehouse() {
        Map<String, String> properties = new HashMap<>();
        Double amountOfResourcesStored = resourcesUtils.getWarehouseCapacityForLevel(1);
        properties.put(BuildingsPropertiesNames.WAREHOUSE_CAPACITY.getLabel(), amountOfResourcesStored.toString());


        return Building.builder()
                .type(BuildingNames.WAREHOUSE.getLabel())
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

    public Building generateMainBuilding() {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

        return Building.builder()
                .type(BuildingNames.MAIN_BUILDING.getLabel())
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

    public Building generateDefenseCenter() {
        Map<String, String> properties = new HashMap<>();
        properties.put("123", "123");

        return Building.builder()
                .type(BuildingNames.DEFENSE_CENTER.getLabel())
                .level(1)
                .score(1)
                .properties(properties)
                .build();
    }

}
