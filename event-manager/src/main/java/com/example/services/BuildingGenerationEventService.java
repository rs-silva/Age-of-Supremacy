package com.example.services;

import com.example.dto.BuildingGenerationEventDTO;
import com.example.mappers.BuildingGenerationEventMapper;
import com.example.models.BuildingGenerationEvent;
import com.example.repositories.BuildingGenerationEventRepository;
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
public class BuildingGenerationEventService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingGenerationEventService.class);

    private final BuildingGenerationEventRepository buildingGenerationEventRepository;

    private final RestTemplate restTemplate;

    public BuildingGenerationEventService(BuildingGenerationEventRepository buildingGenerationEventRepository, RestTemplate restTemplate) {
        this.buildingGenerationEventRepository = buildingGenerationEventRepository;
        this.restTemplate = restTemplate;
    }

    public void registerEvent(BuildingGenerationEventDTO buildingGenerationEventDTO) {
        BuildingGenerationEvent buildingGenerationEvent = BuildingGenerationEventMapper.fromDtoToEntity(buildingGenerationEventDTO);
        buildingGenerationEventRepository.save(buildingGenerationEvent);
    }

    /* Runs once a second to check if there are any buildings which construction time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkBuildingsGenerationEvents() {
        //LOG.info("Running scheduler");

        List<BuildingGenerationEvent> buildingGenerationEventList = buildingGenerationEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to create the building */
        for (BuildingGenerationEvent buildingGenerationEvent : buildingGenerationEventList) {
            LOG.info("Triggering event to complete construction for new building with name {} in base {}", buildingGenerationEvent.getBuildingType(), buildingGenerationEvent.getBaseId());
            /* TODO Remove hardcoded url */
            String url = "http://localhost:8082/api/base/" + buildingGenerationEvent.getBaseId() + "/finishBuilding/" + buildingGenerationEvent.getBuildingType();
            restTemplate.postForObject(url, null, String.class);
            buildingGenerationEventRepository.delete(buildingGenerationEvent);
        }

    }

}
