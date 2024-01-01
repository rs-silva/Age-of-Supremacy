package com.example.services;

import com.example.dto.ArmyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AttackArmyService {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyService.class);

    private final BattleService battleService;

    public AttackArmyService(BattleService battleService) {
        this.battleService = battleService;
    }

    public void addAttackArmy(UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        boolean isBattleInProgress = battleService.isBattleInProgress(destinationBaseId);

        /* If there is a battle already ongoing, join this army in the attacking side */
        if (isBattleInProgress) {

        }
        /* In case there isn't a battle already ongoing, start one */
        else {
            battleService.generateBattle(originBaseId, destinationBaseId, armyDTO);
        }

    }


}
