package com.example.services;

import com.example.dto.UnitsRecruitmentEventDTO;
import com.example.mappers.UnitRecruitmentEventMapper;
import com.example.models.UnitRecruitmentEvent;
import com.example.repositories.UnitRecruitmentEventRepository;
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
public class SupportArmyEventService {

    private static final Logger LOG = LoggerFactory.getLogger(SupportArmyEventService.class);

    private final RestTemplate restTemplate;

    public SupportArmyEventService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void registerEvent(UnitsRecruitmentEventDTO unitsRecruitmentEventDTO) {
        /* TODO check if there are already units being recruited in this base
        *   and add the time correspondingly */
        UnitRecruitmentEvent unitRecruitmentEvent = UnitRecruitmentEventMapper.fromDtoToEntity(unitsRecruitmentEventDTO);
        unitRecruitmentEventRepository.save(unitRecruitmentEvent);
    }

    /* Runs once a second to check if there are any buildings which construction time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkUnitRecruitmentEvents() {
        //LOG.info("Running scheduler");

        List<UnitRecruitmentEvent> unitRecruitmentEventList = unitRecruitmentEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to recruit the units */
        for (UnitRecruitmentEvent unitRecruitmentEvent : unitRecruitmentEventList) {
            LOG.info("Triggering event to complete recruitment of units {} for base with id {}", unitRecruitmentEvent.getUnits(), unitRecruitmentEvent.getBaseId());
            /* TODO Remove hardcoded url */
            String url = "http://localhost:8082/api/base/" + unitRecruitmentEvent.getBaseId() + "/completeUnitsRecruitment";
            UnitsRecruitmentEventDTO unitsRecruitmentEventDTO = UnitRecruitmentEventMapper.fromEntityToDto(unitRecruitmentEvent);
            restTemplate.postForObject(url, unitsRecruitmentEventDTO, UnitsRecruitmentEventDTO.class);
            unitRecruitmentEventRepository.delete(unitRecruitmentEvent);
        }

    }

}
