package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.dto.ArmyMovementEventDTO;
import com.example.mappers.AttackArmyEventMapper;
import com.example.models.AttackArmyEvent;
import com.example.repositories.AttackArmyEventRepository;
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
public class AttackArmyEventService {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyEventService.class);

    private final AttackArmyEventRepository attackArmyEventRepository;

    private final RestTemplate restTemplate;

    public AttackArmyEventService(AttackArmyEventRepository attackArmyEventRepository, RestTemplate restTemplate) {
        this.attackArmyEventRepository = attackArmyEventRepository;
        this.restTemplate = restTemplate;
    }

    public void registerEvent(ArmyMovementEventDTO armyMovementEventDTO) {
        AttackArmyEvent attackArmyEvent = AttackArmyEventMapper.fromDtoToEntity(armyMovementEventDTO);
        attackArmyEventRepository.save(attackArmyEvent);
    }

    /* Runs once a second to check if there are any attack army events which completion time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkAttackArmyEvents() {
        //LOG.info("Running scheduler");

        List<AttackArmyEvent> attackArmyEventList = attackArmyEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to combat-manager with the attack units
        * or to base-manager to return the units back to the origin base */
        for (AttackArmyEvent attackArmyEvent : attackArmyEventList) {
            UUID ownerPlayerId = attackArmyEvent.getOwnerPlayerId();
            UUID ownerBaseId = attackArmyEvent.getOwnerBaseId();
            UUID originBaseId = attackArmyEvent.getOriginBaseId();
            UUID destinationBaseId = attackArmyEvent.getDestinationBaseId();

            String url;

            /* In case it is a send event */
            if (ownerBaseId.equals(originBaseId)) {
                LOG.info("Triggering event to complete attack army deployment from base {} to base {}. {}", attackArmyEvent.getOriginBaseId(), attackArmyEvent.getDestinationBaseId(), attackArmyEvent.getUnits());
                /* TODO Remove hardcoded url */
                url = "http://localhost:8084/api/attackArmy/completeSend/player/" + ownerPlayerId + "/from/" + originBaseId + "/to/" + destinationBaseId;
            }
            /* In case it is a return event */
            else {
                LOG.info("Triggering event to complete attack army return to owner base {}. {}", attackArmyEvent.getOwnerBaseId(), attackArmyEvent.getUnits());
                /* TODO Remove hardcoded url */
                url = "http://localhost:8082/api/attackArmy/completeReturn/" + ownerBaseId;
            }

            ArmyDTO armyDTO = ArmyDTO.builder()
                    .units(attackArmyEvent.getUnits())
                    .build();

            restTemplate.postForObject(url, armyDTO, ArmyDTO.class);

            attackArmyEventRepository.delete(attackArmyEvent);
        }

    }

}
