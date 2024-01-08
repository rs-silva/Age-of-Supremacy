package com.example.utils;

import com.example.dto.ArmyDTO;
import com.example.models.Battle;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class BattleUtils {

    private final RestTemplate restTemplate;

    public BattleUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void getBaseOwnUnits(Battle battle) {
        UUID baseId = battle.getBaseId();

        /* TODO Remove hardcoded url */
        /* Send Support Army Event to event-manager module */
        String url = "http://localhost:8082/api/event/supportArmy";
        ArmyDTO armyDTO = restTemplate.getForEntity(url, ArmyDTO.class).getBody();


    }

}
