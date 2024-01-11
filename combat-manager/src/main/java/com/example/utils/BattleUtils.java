package com.example.utils;

import com.example.dto.BattleNewUnitsForNextRoundDTO;
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

    public BattleNewUnitsForNextRoundDTO getBaseCurrentUnitsForNextRound(Battle battle) {
        UUID baseId = battle.getBaseId();

        /* TODO Remove hardcoded url */
        /* Get current units sit in the base from the base-manager module */
        String url = "http://localhost:8082/api/battle/" + baseId + "/getUnitsForNextRound";
        return restTemplate.getForEntity(url, BattleNewUnitsForNextRoundDTO.class).getBody();
    }

    public int getBaseDefenseHealthPoints(UUID baseId) {
        /* TODO Remove hardcoded url */
        /* Get defense Health Points for this base from the base-manager module */
        String url = "http://localhost:8082/api/battle/" + baseId + "/getBaseDefenseHealthPoints";
        return restTemplate.getForEntity(url, Integer.class).getBody();
    }

}
