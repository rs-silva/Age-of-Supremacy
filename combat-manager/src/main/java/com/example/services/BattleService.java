package com.example.services;

import com.example.dto.ArmyExtendedDTO;
import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.dto.UnitDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
import com.example.utils.ArmyUtils;
import com.example.utils.battle.BattleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
        BaseDefenseInformationDTO baseDefenseInformation = battleUtils.getBaseDefenseInformation(baseId);

        Battle battle = Battle.builder()
                .baseId(baseId)
                .groundDefensePower(baseDefenseInformation.getGroundDefensePower())
                .armoredDefensePower(baseDefenseInformation.getArmoredDefensePower())
                .airDefensePower(baseDefenseInformation.getAirDefensePower())
                .defenseHealthPoints(baseDefenseInformation.getDefenseHealthPoints())
                .armies(new ArrayList<>())
                .build();

        return battleRepository.save(battle);
    }

    /* Runs the next round for each battle occurring */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void runNextRoundForEachBattle() {
        List<Battle> battleList = battleRepository.findAll();
        long now = Instant.now().toEpochMilli();

        for (Battle battle : battleList) {
            LOG.info("Battle {} next round", battle);
            UUID battleId = battle.getId();
            UUID baseId = battle.getBaseId();

            setupRoundNewUnits(battle);

            List<Army> attackingArmies = armyService.findByBattleIdAndRole(battleId, ArmyRole.ATTACKING);
            List<Army> defendingArmies = armyService.findByBattleIdAndRole(battleId, ArmyRole.DEFENDING);

            LOG.info("BEFORE Attacking Armies = {}", attackingArmies.toString());
            LOG.info("BEFORE Defending Armies = {}", defendingArmies.toString());

            List<Army> attackingFrontLine = battleUtils.setupFrontLine(attackingArmies);
            List<Army> defendingFrontLine = battleUtils.setupFrontLine(defendingArmies);

            LOG.info("Attacking Front Line = {}", attackingFrontLine.toString());
            LOG.info("Defending Front Line = {}", defendingFrontLine.toString());

            LOG.info("AFTER Attacking Armies = {}", attackingArmies.toString());
            LOG.info("AFTER Defending Armies = {}", defendingArmies.toString());

            /* If the base defenses are still active, the attacking armies cannot attack the defending armies */
            if (battleUtils.areBaseDefensesActive(battle)) {
                int totalAttackPower = battleUtils.calculateAttackingPowerToBaseDefenses(attackingFrontLine);
                LOG.info("totalAttackPower = {}", totalAttackPower);
                battleUtils.updateBaseDefensesHealthPoints(battle, totalAttackPower);

                int groundUnitsDefensePower = battle.getGroundDefensePower();
                int attackingFrontLineGroundUnitsDefense = battleUtils.getArmiesGroundUnitsMetric(attackingFrontLine, UnitDTO::getDefense);
                LOG.info("attackingFrontLineGroundUnitsDefense = {}", attackingFrontLineGroundUnitsDefense);
                int groundDefensesEffectiveDamage = Math.max(groundUnitsDefensePower - attackingFrontLineGroundUnitsDefense, 0);
                LOG.info("groundDefensesEffectiveDamage = {}", groundDefensesEffectiveDamage);

                int armoredUnitsDefensePower = battle.getArmoredDefensePower();
                int attackingFrontLineArmoredUnitsDefense = battleUtils.getArmiesArmoredUnitsMetric(attackingFrontLine, UnitDTO::getDefense);
                LOG.info("attackingFrontLineArmoredUnitsDefense = {}", attackingFrontLineArmoredUnitsDefense);
                int armoredDefensesEffectiveDamage = Math.max(armoredUnitsDefensePower - attackingFrontLineArmoredUnitsDefense, 0);
                LOG.info("armoredDefensesEffectiveDamage = {}", armoredDefensesEffectiveDamage);

                int airUnitsDefensePower = battle.getAirDefensePower();
                int attackingFrontLineAirUnitsDefense = battleUtils.getArmiesAirUnitsMetric(attackingFrontLine, UnitDTO::getDefense);
                LOG.info("attackingFrontLineAirUnitsDefense = {}", attackingFrontLineAirUnitsDefense);
                int airDefensesEffectiveDamage = Math.max(airUnitsDefensePower - attackingFrontLineAirUnitsDefense, 0);
                LOG.info("airDefensesEffectiveDamage = {}", airDefensesEffectiveDamage);
            }
            /* If the base defenses are not active, the attacking and the defending armies will attack each other */
            else {
                LOG.info("BASE DEFENSES ARE DOWN!");
                boolean checkIfFrontLinesAreFull = battleUtils.checkIfFrontLinesAreFull(attackingFrontLine, defendingFrontLine);
                LOG.info("checkIfFrontLinesAreFull = {}", checkIfFrontLinesAreFull);
            }
        }

        LOG.info("Battles processing time = {}ms", Instant.now().toEpochMilli() - now);
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
