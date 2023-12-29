package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.dto.ArmyMovementEventDTO;
import com.example.mappers.SupportArmyEventMapper;
import com.example.models.SupportArmyEvent;
import com.example.repositories.SupportArmyEventRepository;
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
public class SupportArmyEventService {

    private static final Logger LOG = LoggerFactory.getLogger(SupportArmyEventService.class);

    private final SupportArmyEventRepository supportArmyEventRepository;

    private final RestTemplate restTemplate;

    public SupportArmyEventService(SupportArmyEventRepository supportArmyEventRepository, RestTemplate restTemplate) {
        this.supportArmyEventRepository = supportArmyEventRepository;
        this.restTemplate = restTemplate;
    }

    public void registerEvent(ArmyMovementEventDTO armyMovementEventDTO) {
        SupportArmyEvent supportArmyEvent = SupportArmyEventMapper.fromDtoToEntity(armyMovementEventDTO);
        supportArmyEventRepository.save(supportArmyEvent);
    }

    /* Runs once a second to check if there are any support army events which completion time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkSupportArmyEvents() {
        //LOG.info("Running scheduler");

        List<SupportArmyEvent> supportArmyEventList = supportArmyEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to recruit the units */
        for (SupportArmyEvent supportArmyEvent : supportArmyEventList) {
            UUID ownerBaseId = supportArmyEvent.getOwnerBaseId();
            UUID originBaseId = supportArmyEvent.getOriginBaseId();
            UUID destinationBaseId = supportArmyEvent.getDestinationBaseId();

            String url;

            /* In case it is a send event */
            if (ownerBaseId.equals(originBaseId)) {
                LOG.info("Triggering event to complete support army deployment from base {} to base {}. {}", supportArmyEvent.getOriginBaseId(), supportArmyEvent.getDestinationBaseId(), supportArmyEvent.getUnits());
                /* TODO Remove hardcoded url */
                url = "http://localhost:8082/api/supportArmy/completeSend/" + originBaseId + "/to/" + destinationBaseId;
            }
            /* In case it is a return event */
            else {
                LOG.info("Triggering event to complete support army return to owner base {}. {}", supportArmyEvent.getOwnerBaseId(), supportArmyEvent.getUnits());
                /* TODO Remove hardcoded url */
                url = "http://localhost:8082/api/supportArmy/completeReturn/" + ownerBaseId;
            }

            ArmyDTO armyDTO = ArmyDTO.builder()
                    .units(supportArmyEvent.getUnits())
                    .build();

            restTemplate.postForObject(url, armyDTO, ArmyDTO.class);

            supportArmyEventRepository.delete(supportArmyEvent);
        }

    }

}
