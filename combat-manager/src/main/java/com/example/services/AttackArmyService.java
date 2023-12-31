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

        if (isBattleInProgress) {

        }
        else {

        }

    }


}
