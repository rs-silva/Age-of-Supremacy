package com.example.services;

import com.example.dto.ArmyExtendedDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
import com.example.utils.ArmyUtils;
import com.example.utils.BattleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("combat-manager microservice BattleService")
public class BattleService {

    private static final Logger LOG = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;

    private final BattleUtils battleUtils;

    private final ArmyService armyService;

    public BattleService(BattleRepository battleRepository, BattleUtils battleUtils, ArmyService armyService) {
        this.battleRepository = battleRepository;
        this.battleUtils = battleUtils;
        this.armyService = armyService;
    }

    public Battle generateBattle(UUID baseId) {
        Battle battle = Battle.builder()
                .baseId(baseId)
                .defenseHealthPoints(500) /* TODO get defense HP */
                .armies(new ArrayList<>())
                .build();

        return battleRepository.save(battle);
    }

    /* Runs the next round for each battle occurring */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void runNextRoundForEachBattle() {
        List<Battle> battleList = battleRepository.findAll();

        for (Battle battle : battleList) {
            UUID battleId = battle.getId();
            UUID baseId = battle.getBaseId();

            setupRoundNewUnits(battle);

            List<Army> attackingArmies = armyService.findByBattleIdAndRole(battleId, ArmyRole.ATTACKING);
            List<Army> defendingArmies = armyService.findByBattleIdAndRole(battleId, ArmyRole.DEFENDING);
        }
    }

    private void setupRoundNewUnits(Battle battle) {
        /* Fetch the new own units and/or support armies in the base from base-manager */
        BattleNewUnitsForNextRoundDTO battleNewUnitsForNextRoundDTO = battleUtils.getBaseCurrentUnitsForNextRound(battle);

        /* Update armies in the base */
        for (ArmyExtendedDTO newArmy : battleNewUnitsForNextRoundDTO.getSupportArmies()) {
            Army currentArmy = armyService.findByBattleIdAndOwnerBaseId(battle.getId(), newArmy.getOwnerBaseId());

            /* In case there isn't an army from this owner base in this battle, create one */
            if (currentArmy == null) {
                armyService.generateDefendingArmy(newArmy.getOwnerPlayerId(), newArmy.getOwnerBaseId(), newArmy.getUnits(), battle);
            }
            /* In case there is already an army from this base in the battle, add the new units */
            else {
                Map<String, Integer> updatedArmy = ArmyUtils.addUnitsToArmy(currentArmy.getUnits(), newArmy.getUnits());
                currentArmy.setUnits(updatedArmy);
            }
        }

    }

    public Battle findByBaseId(UUID baseId) {
        return battleRepository.findByBaseId(baseId);
    }

}
