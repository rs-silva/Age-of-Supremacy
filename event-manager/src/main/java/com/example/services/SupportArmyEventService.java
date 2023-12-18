package com.example.services;

import com.example.dto.SupportArmyEventDTO;
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

    public void registerEvent(SupportArmyEventDTO supportArmyEventDTO) {
        SupportArmyEvent supportArmyEvent = SupportArmyEventMapper.fromDtoToEntity(supportArmyEventDTO);
        supportArmyEventRepository.save(supportArmyEvent);
    }

    /* Runs once a second to check if there are any support army events which completion time already passed */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkSupportArmyEvents() {
        //LOG.info("Running scheduler");

        List<SupportArmyEvent> supportArmyEventList = supportArmyEventRepository.findByCompletionTimeBefore(Timestamp.from(Instant.now()));

        /* Trigger an event that sends a call to base-manager to recruit the units */
        /*for (SupportArmyEvent supportArmyEvent : supportArmyEventList) {
            UUID ownerBaseId = supportArmyEvent.getOwnerBaseId();
            UUID originBaseId = supportArmyEvent.getOriginBaseId();

            /* In case it is a send event */
            /*if (ownerBaseId.equals(originBaseId)) {
                LOG.info("Triggering event to complete support army deployment from base {} to base {}. {}", supportArmyEvent.getOriginBaseId(), supportArmyEvent.getDestinationBaseId(), supportArmyEvent.getUnits());
                /* TODO Remove hardcoded url */
                /*String url = "http://localhost:8082/api/base/" + unitRecruitmentEvent.getBaseId() + "/completeUnitsRecruitment";
                UnitsRecruitmentEventDTO unitsRecruitmentEventDTO = UnitRecruitmentEventMapper.fromEntityToDto(unitRecruitmentEvent);
                restTemplate.postForObject(url, unitsRecruitmentEventDTO, UnitsRecruitmentEventDTO.class);
                unitRecruitmentEventRepository.delete(unitRecruitmentEvent);
            }
            /* In case it is a return event */




        //}

    }

}
