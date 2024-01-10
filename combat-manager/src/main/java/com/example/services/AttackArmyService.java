package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.models.Battle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AttackArmyService {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyService.class);

    private final BattleService battleService;

    private final ArmyService armyService;

    public AttackArmyService(BattleService battleService, ArmyService armyService) {
        this.battleService = battleService;
        this.armyService = armyService;
    }

    @Transactional
    public void addAttackArmy(UUID ownerPlayerId, UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        Battle battle = battleService.findByBaseId(destinationBaseId);

        /* If there is a battle already ongoing, join this army in the attacking side */
        if (battle != null) {
            /* TODO Check via attacking owner base id if there is already an attack army from this base */
        }
        /* In case there isn't a battle already ongoing, start one */
        else {
            Battle newBattle = battleService.generateBattle(destinationBaseId);
            armyService.generateAttackingArmy(ownerPlayerId, originBaseId, armyDTO, newBattle);
        }

    }

}
