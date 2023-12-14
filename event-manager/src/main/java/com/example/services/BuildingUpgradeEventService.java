package com.example.services;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.exceptions.InternalServerErrorException;
import com.example.mappers.BuildingUpgradeEventMapper;
import com.example.models.BuildingUpgradeEvent;
import com.example.repositories.BuildingUpgradeEventRepository;
import com.example.utils.EventManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BuildingUpgradeEventService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingUpgradeEventService.class);

    private final BuildingUpgradeEventRepository buildingUpgradeEventRepository;

    private final RestTemplate restTemplate;

    public BuildingUpgradeEventService(BuildingUpgradeEventRepository buildingUpgradeEventRepository, RestTemplate restTemplate) {
        this.buildingUpgradeEventRepository = buildingUpgradeEventRepository;
        this.restTemplate = restTemplate;
    }

    public void registerEvent(BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        /* TODO Check possible buildings being upgraded/constructed in the same base and increment the completion time */
        checkIfBuildingIsNotBeingUpgradedAlready(buildingUpgradeEventDTO.getBuildingId());

        BuildingUpgradeEvent buildingUpgradeEvent = BuildingUpgradeEventMapper.fromDtoToEntity(buildingUpgradeEventDTO);
        buildingUpgradeEventRepository.save(buildingUpgradeEvent);
    }

    /* Runs once a second to check if there are any buildings which construction time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkBuildingsUpgradeEvents() {
        //LOG.info("Running scheduler");

        List<BuildingUpgradeEvent> buildingUpgradeEventList = buildingUpgradeEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to level up the building */
        for (BuildingUpgradeEvent buildingUpgradeEvent : buildingUpgradeEventList) {
            /* In case it is an event that upgrades a building that already exists */
            if (buildingUpgradeEvent.getBuildingId() != null) {
                LOG.info("Triggering event to complete upgrade for building with id {}", buildingUpgradeEvent.getBuildingId());
                /* TODO Remove hardcoded url */
                String url = "http://localhost:8082/api/building/completeUpgrade/" + buildingUpgradeEvent.getBuildingId();
                restTemplate.postForObject(url, null, String.class);
                buildingUpgradeEventRepository.delete(buildingUpgradeEvent);
            }
            /* In case it is a building generation event (construct a building that does not exist yet) */
            else {
                LOG.info("Triggering event to complete construction for new building with name {} in base {}", buildingUpgradeEvent.getBuildingType(), buildingUpgradeEvent.getBaseId());
                /* TODO Remove hardcoded url */
                String url = "http://localhost:8082/api/base/" + buildingUpgradeEvent.getBaseId() + "/finishBuilding/" + buildingUpgradeEvent.getBuildingType();
                restTemplate.postForObject(url, null, String.class);
                buildingUpgradeEventRepository.delete(buildingUpgradeEvent);
            }
        }

    }

    private void checkIfBuildingIsNotBeingUpgradedAlready(UUID id) {
        BuildingUpgradeEvent buildingUpgradeEvent = buildingUpgradeEventRepository.findByBuildingId(id);

        if (buildingUpgradeEvent != null) {
            throw new InternalServerErrorException(EventManagerConstants.BUILDING_UPGRADE_EVENT_ALREADY_EXISTS);
        }

    }

}
