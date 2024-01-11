package com.example.services;

import com.example.dto.ArmySimpleDTO;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.utils.ArmyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
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
    public void addAttackArmy(UUID ownerPlayerId, UUID originBaseId, UUID destinationBaseId, ArmySimpleDTO armySimpleDTO) {
        Battle battle = battleService.findByBaseId(destinationBaseId);

        /* If there is a battle already ongoing, join this army in the attacking side */
        if (battle != null) {
            /* TODO Check via attacking owner base id if there is already an attack army from this base */
            Army currentArmy = armyService.findByBattleIdAndOwnerBaseId(battle.getId(), originBaseId);

            /* In case there isn't an army from this owner base in this battle, create one */
            if (currentArmy == null) {
                armyService.generateAttackingArmy(ownerPlayerId, originBaseId, armySimpleDTO.getUnits(), battle);
            }
            /* In case there is already an army from this base in the battle, add the new units */
            else {
                Map<String, Integer> updatedArmy = ArmyUtils.addUnitsToArmy(currentArmy.getUnits(), armySimpleDTO.getUnits());
                currentArmy.setUnits(updatedArmy);
            }
        }
        /* In case there isn't a battle already ongoing, start one */
        else {
            Battle newBattle = battleService.generateBattle(destinationBaseId);
            armyService.generateAttackingArmy(ownerPlayerId, originBaseId, armySimpleDTO.getUnits(), newBattle);
        }

    }

}
