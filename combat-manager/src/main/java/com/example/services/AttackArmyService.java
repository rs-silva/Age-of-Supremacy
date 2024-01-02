package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.repositories.ArmyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AttackArmyService {

    private static final Logger LOG = LoggerFactory.getLogger(AttackArmyService.class);

    private final BattleService battleService;

    private final ArmyRepository armyRepository;

    public AttackArmyService(BattleService battleService, ArmyRepository armyRepository) {
        this.battleService = battleService;
        this.armyRepository = armyRepository;
    }

    @Transactional
    public void addAttackArmy(UUID ownerPlayerId, UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        boolean isBattleInProgress = battleService.isBattleInProgress(destinationBaseId);

        /* If there is a battle already ongoing, join this army in the attacking side */
        if (isBattleInProgress) {

        }
        /* In case there isn't a battle already ongoing, start one */
        else {
            Army army = generateAttackingArmy(ownerPlayerId, originBaseId, armyDTO);
            battleService.generateBattle(army, destinationBaseId);
        }

    }

    private Army generateAttackingArmy(UUID ownerPlayerId, UUID originBaseId, ArmyDTO armyDTO) {
        Army army = Army.builder()
                .ownerPlayerId(ownerPlayerId)
                .ownerBaseId(originBaseId)
                .role(ArmyRole.ATTACKING)
                .units(armyDTO.getUnits())
                .build();

        return armyRepository.save(army);
    }

}
