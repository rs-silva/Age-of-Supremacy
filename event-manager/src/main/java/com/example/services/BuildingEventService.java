package com.example.services;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.mappers.BuildingEventMapper;
import com.example.models.BuildingUpgradeEvent;
import com.example.repositories.BuildingUpgradeEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class BuildingEventService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingEventService.class);

    private final BuildingUpgradeEventRepository buildingUpgradeEventRepository;

    private final RestTemplate restTemplate;

    public BuildingEventService(BuildingUpgradeEventRepository buildingUpgradeEventRepository, RestTemplate restTemplate) {
        this.buildingUpgradeEventRepository = buildingUpgradeEventRepository;
        this.restTemplate = restTemplate;
    }

    public void registerEvent(BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        BuildingUpgradeEvent buildingUpgradeEvent = BuildingEventMapper.fromDtoToEntity(buildingUpgradeEventDTO);
        buildingUpgradeEventRepository.save(buildingUpgradeEvent);
    }

    /* Runs once a second to check if there are any buildings, which construction time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkBuildingsUpgrades() {
        //LOG.info("Running scheduler");

        List<BuildingUpgradeEvent> buildingUpgradeEventList = buildingUpgradeEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to level up the building */
        for (BuildingUpgradeEvent buildingUpgradeEvent : buildingUpgradeEventList) {
            LOG.info("Triggering event to complete upgrade for building with id {}", buildingUpgradeEvent.getBuildingId());
            /* TODO Remove hardcoded url */
            String url = "http://localhost:8082/api/building/completeUpgrade/" + buildingUpgradeEvent.getBuildingId();
            restTemplate.postForObject(url, null, String.class);
            buildingUpgradeEventRepository.delete(buildingUpgradeEvent);
        }

    }

}
