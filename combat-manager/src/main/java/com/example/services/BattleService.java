package com.example.services;

import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
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

    public BattleService(BattleRepository battleRepository) {
        this.battleRepository = battleRepository;
    }

    public void generateBattle(Army attackingArmy, UUID destinationBaseId) {
        Battle battle = Battle.builder()
                .baseId(destinationBaseId)
                .defenseHealthPoints(500) /* TODO get defense HP */
                .armies(new ArrayList<>())
                .build();

        attackingArmy.setBattle(battle);

        battleRepository.save(battle);
    }

    /* Runs the next round for each battle occurring */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void runNextRoundForEachBattle() {
        List<Battle> battleList = battleRepository.findAll();

        for (Battle battle : battleList) {

        }
    }

    public boolean isBattleInProgress(UUID baseId) {
        Battle battle = battleRepository.findByBaseId(baseId);

        return battle != null;
    }

}
