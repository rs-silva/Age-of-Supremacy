package com.example.services;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.mappers.BuildingEventMapper;
import com.example.models.BuildingUpgradeEvent;
import com.example.repositories.BuildingEventRepository;
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

    private final BuildingEventRepository buildingEventRepository;

    private final RestTemplate restTemplate;

    public BuildingEventService(BuildingEventRepository buildingEventRepository, RestTemplate restTemplate) {
        this.buildingEventRepository = buildingEventRepository;
        this.restTemplate = restTemplate;
    }

    public void registerEvent(BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        BuildingUpgradeEvent buildingUpgradeEvent = BuildingEventMapper.fromDtoToEntity(buildingUpgradeEventDTO);
        buildingEventRepository.save(buildingUpgradeEvent);
    }

    /* Runs once a second to check if there are any buildings, which construction time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkBuildingsUpgrades() {
        LOG.info("Running scheduler");

        List<BuildingUpgradeEvent> buildingUpgradeEventList = buildingEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to level up the building */
        for (BuildingUpgradeEvent buildingUpgradeEvent : buildingUpgradeEventList) {
            LOG.info("Triggering building upgrade event for building with id {}", buildingUpgradeEvent.getBuildingId());
            /* TODO Remove hardcoded url */
            String url = "http://localhost:8082/api/building/completeUpgrade/" + buildingUpgradeEvent.getBuildingId();
            restTemplate.postForObject(url, null, String.class);
            buildingEventRepository.delete(buildingUpgradeEvent);
        }

    }

}
