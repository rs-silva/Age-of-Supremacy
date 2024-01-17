package com.example.services;

import com.example.dto.ArmyExtendedDTO;
import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
import com.example.utils.ArmyUtils;
import com.example.utils.BattleFrontLineUnitsLimits;
import com.example.utils.BattleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("combat-manager microservice BattleService")
public class BattleService {

    private static final Logger LOG = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;

    private final BattleUtils battleUtils;

    private final ArmyService armyService;

    private final Map<String, Integer> frontLineUnitsLimits;

    public BattleService(BattleRepository battleRepository, BattleUtils battleUtils, ArmyService armyService) {
        this.battleRepository = battleRepository;
        this.battleUtils = battleUtils;
        this.armyService = armyService;
        this.frontLineUnitsLimits = BattleFrontLineUnitsLimits.getFrontLineUnitsLimits();
    }

    public Battle generateBattle(UUID baseId) {
        BaseDefenseInformationDTO baseDefenseInformation = battleUtils.getBaseDefenseInformation(baseId);

        Battle battle = Battle.builder()
                .baseId(baseId)
                .groundDefensePower(baseDefenseInformation.getGroundDefensePower())
                .antiTankDefensePower(baseDefenseInformation.getAntiTankDefensePower())
                .antiAirDefensePower(baseDefenseInformation.getAntiAirDefensePower())
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

        for (Battle battle : battleList) {
            LOG.info("Battle {} next round", battle);
            UUID battleId = battle.getId();
            UUID baseId = battle.getBaseId();

            setupRoundNewUnits(battle);

            List<Army> attackingArmies = armyService.findByBattleIdAndRole(battleId, ArmyRole.ATTACKING);
            List<Army> defendingArmies = armyService.findByBattleIdAndRole(battleId, ArmyRole.DEFENDING);

            LOG.info("BEFORE Attacking Armies = {}", attackingArmies.toString());
            LOG.info("BEFORE Defending Armies = {}", defendingArmies.toString());

            List<Army> attackingFrontLine = setupFrontLine(attackingArmies);
            List<Army> defendingFrontLine = setupFrontLine(defendingArmies);

            LOG.info("Attacking Front Line = {}", attackingFrontLine.toString());
            LOG.info("Defending Front Line = {}", defendingFrontLine.toString());

            LOG.info("AFTER Attacking Armies = {}", attackingArmies.toString());
            LOG.info("AFTER Defending Armies = {}", defendingArmies.toString());
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

    private List<Army> setupFrontLine(List<Army> armies) {
        // Create a map to store the count of units for each type in the front line
        Map<String, Integer> frontLineUnitCounts = new HashMap<>();
        List<Army> frontLineArmies = new ArrayList<>();

        // Sort armies based on some criteria (e.g., total attack power)
        //armies.sort(Comparator.comparingInt(this::calculateTotalAttackPower).reversed());

        // Iterate through armies and add units to the front line respecting type-specific limits
        for (Army army : armies) {
            Map<String, Integer> armyUnits = army.getUnits();

            Army newFrontLineArmy = new Army();
            newFrontLineArmy.setOwnerBaseId(army.getOwnerBaseId());
            newFrontLineArmy.setOwnerPlayerId(army.getOwnerPlayerId());
            Map<String, Integer> newFrontLineArmyUnits = new HashMap<>();

            for (Map.Entry<String, Integer> entry : armyUnits.entrySet()) {
                String unitType = entry.getKey();
                int unitCount = entry.getValue();

                int unitTypeLimit = frontLineUnitsLimits.get(unitType);

                int unitsToAdd = Math.min(unitCount, unitTypeLimit - frontLineUnitCounts.getOrDefault(unitType, 0));
                if (unitsToAdd > 0) {
                    newFrontLineArmyUnits.put(unitType, unitsToAdd);

                    frontLineUnitCounts.put(unitType, frontLineUnitCounts.getOrDefault(unitType, 0) + unitsToAdd);

                    int unitOriginalAmount = armyUnits.get(unitType);
                    armyUnits.put(unitType, unitOriginalAmount - unitsToAdd);
                }
            }

            newFrontLineArmy.setUnits(newFrontLineArmyUnits);
            frontLineArmies.add(newFrontLineArmy);

            //armyService.save(army);
        }

        return frontLineArmies;
    }

    public Battle findByBaseId(UUID baseId) {
        return battleRepository.findByBaseId(baseId);
    }

}
