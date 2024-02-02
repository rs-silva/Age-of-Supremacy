package com.example.services;

import com.example.dto.ArmyExtendedDTO;
import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
import com.example.utils.ArmyUtils;
import com.example.utils.battle.ActiveDefensesPhaseUtils;
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

    private final ActiveDefensesPhaseUtils activeDefensesPhaseUtils;

    public BattleService(BattleRepository battleRepository, BattleUtils battleUtils, ArmyService armyService, ActiveDefensesPhaseUtils activeDefensesPhaseUtils) {
        this.battleRepository = battleRepository;
        this.battleUtils = battleUtils;
        this.armyService = armyService;
        this.activeDefensesPhaseUtils = activeDefensesPhaseUtils;
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
    @Scheduled(fixedRate = 5000)
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

            /* If the base defenses are still active, the attacking armies cannot attack the defending armies */
            if (battleUtils.areBaseDefensesActive(battle)) {
                int totalAttackPower = battleUtils.calculateAttackingPowerToBaseDefenses(attackingFrontLine);
                LOG.info("totalAttackPower = {}", totalAttackPower);
                activeDefensesPhaseUtils.updateBaseDefensesHealthPoints(battle, totalAttackPower);

                int groundUnitsDefensePower = battle.getGroundDefensePower();
                groundUnitsDefensePower = battleUtils.applyScalingFactor(groundUnitsDefensePower);
                activeDefensesPhaseUtils.calculateGroundUnitsLosses(attackingFrontLine, groundUnitsDefensePower);

                int armoredUnitsDefensePower = battle.getArmoredDefensePower();
                armoredUnitsDefensePower = battleUtils.applyScalingFactor(armoredUnitsDefensePower);
                activeDefensesPhaseUtils.calculateArmoredUnitsLosses(attackingFrontLine, armoredUnitsDefensePower);

                int airUnitsDefensePower = battle.getAirDefensePower();
                airUnitsDefensePower = battleUtils.applyScalingFactor(airUnitsDefensePower);
                activeDefensesPhaseUtils.calculateAirUnitsLosses(attackingFrontLine, airUnitsDefensePower);

                mergeFrontLines(attackingArmies, attackingFrontLine);
                mergeFrontLines(defendingArmies, defendingFrontLine);
                cleanEmptyArmies(attackingArmies);

                LOG.info("ATTACKING ARMIES IF = {}", attackingArmies);
                /* End battle with defender winning
                * Return base's own units and support armies to base-manager */
                if (!doArmiesHaveAttackUnits(attackingArmies)) {
                    LOG.info("NO ATTACK UNITS! DEFENDER HAS WON!");
                }
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

    private void endBattleWithDefenderSideWinning(Battle battle) {

    }

    private void cleanEmptyArmies(List<Army> armies) {
        for (Army army : armies) {
            boolean isArmyEmpty = ArmyUtils.isArmyEmpty(army.getUnits());

            if (isArmyEmpty) {
                armyService.deleteArmy(army);
            }
        }
    }

    private boolean doArmiesHaveAttackUnits(List<Army> armies) {
        for (Army army : armies) {
            if (ArmyUtils.doesArmyHaveAttackUnits(army.getUnits())) {
                return true;
            }
        }

        return false;
    }

    private void mergeFrontLines(List<Army> armies, List<Army> frontLine) {
        for (Army frontLineArmy : frontLine) {
            UUID ownerBaseId = frontLineArmy.getOwnerBaseId();
            Army army = armyService.getArmyWithOwnerBaseId(armies, ownerBaseId);

            Map<String, Integer> updatedUnits = ArmyUtils.addUnitsToArmy(army.getUnits(), frontLineArmy.getUnits());
            army.setUnits(updatedUnits);
        }
    }

    public Battle findByBaseId(UUID baseId) {
        return battleRepository.findByBaseId(baseId);
    }

}
