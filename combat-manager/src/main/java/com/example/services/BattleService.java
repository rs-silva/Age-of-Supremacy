package com.example.services;

import com.example.dto.BaseUnitsForNextRoundDTO;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
import com.example.utils.BattleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

            /* Fetch the new own units and/or support armies in the base from base-manager */
            BaseUnitsForNextRoundDTO baseUnitsForNextRoundDTO = battleUtils.getBaseCurrentUnitsForNextRound(battle);

            /* Update base own units */
            Army baseCurrentOwnUnits = armyService.findByBattleIdAndOwnerBaseId(battleId, baseId);
        }
    }

    public Battle findByBaseId(UUID baseId) {
        return battleRepository.findByBaseId(baseId);
    }

}
