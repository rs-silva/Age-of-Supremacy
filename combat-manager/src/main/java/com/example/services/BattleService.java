package com.example.services;

import com.example.dto.ArmyExtendedDTO;
import com.example.dto.BaseDefenseInformationDTO;
import com.example.dto.BaseUnitsDTO;
import com.example.dto.BattleNewUnitsForNextRoundDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.BattleRepository;
import com.example.utils.ArmyUtils;
import com.example.utils.battle.ActiveDefensesPhaseUtils;
import com.example.utils.battle.BattleFrontLineUnitsLimits;
import com.example.utils.battle.EngagementPhaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("combat-manager microservice BattleService")
public class BattleService {

    private static final Logger LOG = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;

    private final ArmyService armyService;

    private final Map<String, Integer> frontLineUnitsLimits;

    private final ActiveDefensesPhaseUtils activeDefensesPhaseUtils;

    private final EngagementPhaseUtils engagementPhaseUtils;

    private final RestTemplate restTemplate;

    public BattleService(BattleRepository battleRepository, ArmyService armyService, ActiveDefensesPhaseUtils activeDefensesPhaseUtils, EngagementPhaseUtils engagementPhaseUtils, RestTemplate restTemplate) {
        this.battleRepository = battleRepository;
        this.armyService = armyService;
        this.restTemplate = restTemplate;
        this.frontLineUnitsLimits = BattleFrontLineUnitsLimits.getFrontLineUnitsLimits();
        this.activeDefensesPhaseUtils = activeDefensesPhaseUtils;
        this.engagementPhaseUtils = engagementPhaseUtils;
    }

    public Battle generateBattle(UUID baseId) {
        BaseDefenseInformationDTO baseDefenseInformation = getBaseDefenseInformation(baseId);

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

            List<Army> attackingFrontLine = setupFrontLine(attackingArmies);
            List<Army> defendingFrontLine = setupFrontLine(defendingArmies);

            LOG.info("Attacking Front Line = {}", attackingFrontLine.toString());
            LOG.info("Defending Front Line = {}", defendingFrontLine.toString());

            /* If the base defenses are still active, the attacking armies cannot attack the defending armies */
            if (areBaseDefensesActive(battle)) {
                int totalAttackPower = activeDefensesPhaseUtils.calculateAttackingPowerToBaseDefenses(attackingFrontLine);
                LOG.info("totalAttackPower = {}", totalAttackPower);
                activeDefensesPhaseUtils.updateBaseDefensesHealthPoints(battle, totalAttackPower);

                int groundDefensePower = activeDefensesPhaseUtils.getGroundDefensePower(battle);
                activeDefensesPhaseUtils.calculateGroundUnitsLosses(attackingFrontLine, groundDefensePower);

                int armoredDefensePower = activeDefensesPhaseUtils.getArmoredDefensePower(battle);
                activeDefensesPhaseUtils.calculateArmoredUnitsLosses(attackingFrontLine, armoredDefensePower);

                int airDefensePower = activeDefensesPhaseUtils.getAirDefensePower(battle);
                activeDefensesPhaseUtils.calculateAirUnitsLosses(attackingFrontLine, airDefensePower);

                mergeFrontLines(attackingArmies, attackingFrontLine);
                mergeFrontLines(defendingArmies, defendingFrontLine);
                cleanEmptyArmies(attackingArmies);

                LOG.info("ATTACKING ARMIES IF = {}", attackingArmies);
                /* End battle with defender winning
                * Return base's own units and support armies to base-manager */
                if (!doArmiesHaveAttackUnits(attackingArmies)) {
                    LOG.info("NO ATTACK UNITS! DEFENDER HAS WON!");
                    endBattleWithDefenderSideWinning(battle);
                }
            }
            /* If the base defenses are not active, the attacking and the defending armies will attack each other */
            else {
                LOG.info("BASE DEFENSES ARE DOWN!");
                boolean checkIfFrontLinesAreFull = checkIfFrontLinesAreFull(attackingFrontLine, defendingFrontLine);
                LOG.info("checkIfFrontLinesAreFull = {}", checkIfFrontLinesAreFull);

                engagementPhaseUtils.calculateArmiesLosses(attackingFrontLine, defendingFrontLine);

                mergeFrontLines(attackingArmies, attackingFrontLine);
                mergeFrontLines(defendingArmies, defendingFrontLine);
                cleanEmptyArmies(attackingArmies);
                cleanEmptyArmies(defendingArmies);
            }

        }

        LOG.info("Battles processing time = {}ms", Instant.now().toEpochMilli() - now);
    }

    private void setupRoundNewUnits(Battle battle) {
        /* Fetch the new own units and/or support armies in the base from base-manager */
        BattleNewUnitsForNextRoundDTO battleNewUnitsForNextRoundDTO = getBaseCurrentUnitsForNextRound(battle);

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
        Map<String, Integer> frontLineUnitsCounter = new HashMap<>();
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
                int unitAmount = entry.getValue();

                int unitTypeLimit = frontLineUnitsLimits.get(unitType);

                int unitsToAdd = Math.min(unitAmount, unitTypeLimit - frontLineUnitsCounter.getOrDefault(unitType, 0));
                if (unitsToAdd > 0) {
                    newFrontLineArmyUnits.put(unitType, unitsToAdd);

                    frontLineUnitsCounter.put(unitType, frontLineUnitsCounter.getOrDefault(unitType, 0) + unitsToAdd);

                    /* Remove from original army */
                    armyUnits.put(unitType, unitAmount - unitsToAdd);
                }
            }

            newFrontLineArmy.setUnits(newFrontLineArmyUnits);
            frontLineArmies.add(newFrontLineArmy);
        }

        return frontLineArmies;
    }

    private void endBattleWithDefenderSideWinning(Battle battle) {
        /* TODO returning Attacking Armies */
        List<Army> attackingArmies = armyService.findByBattleIdAndRole(battle.getId(), ArmyRole.ATTACKING);
        List<Army> defendingArmies = armyService.findByBattleIdAndRole(battle.getId(), ArmyRole.DEFENDING);
        UUID defendingBaseId = battle.getBaseId();

        BaseUnitsDTO baseUnitsToReturn = new BaseUnitsDTO();
        List<ArmyExtendedDTO> supportArmiesList = new ArrayList<>();

        for (Army defendingArmy : defendingArmies) {
            Map<String, Integer> armyUnits = defendingArmy.getUnits();

            /* In case it is the base's own units */
            if (defendingArmy.getOwnerBaseId().equals(defendingBaseId)) {
                baseUnitsToReturn.setOwnUnits(armyUnits);
            }
            /* In case it's a support unit */
            else {
                ArmyExtendedDTO armyExtendedDTO = ArmyExtendedDTO.builder()
                        .ownerPlayerId(defendingArmy.getOwnerPlayerId())
                        .ownerBaseId(defendingArmy.getOwnerBaseId())
                        .units(armyUnits)
                        .build();

                supportArmiesList.add(armyExtendedDTO);
            }
        }

        baseUnitsToReturn.setSupportArmies(supportArmiesList);

        returnSupportArmiesAfterBattle(defendingBaseId, baseUnitsToReturn);

        battleRepository.delete(battle);
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

    private boolean areBaseDefensesActive(Battle battle) {
        return battle.getDefenseHealthPoints() > 0;
    }

    private boolean checkIfFrontLinesAreFull(List<Army> attackingFrontLine, List<Army> defendingFrontLine) {
        /* Checks if both front lines have all the unit types */
        return checkIfFrontLineIsFull(attackingFrontLine) && checkIfFrontLineIsFull(defendingFrontLine);
    }

    private boolean checkIfFrontLineIsFull(List<Army> frontLine) {
        for (Army army : frontLine) {
            if (!ArmyUtils.checkIfArmyHasEveryUnitType(army.getUnits())) {
                return false;
            }
        }

        return true;
    }

    private BaseDefenseInformationDTO getBaseDefenseInformation(UUID baseId) {
        /* TODO Remove hardcoded url */
        /* Get defense information for this base from the base-manager module */
        String url = "http://localhost:8082/api/base/" + baseId + "/getDefenseInformation";
        return restTemplate.getForEntity(url, BaseDefenseInformationDTO.class).getBody();
    }

    public BattleNewUnitsForNextRoundDTO getBaseCurrentUnitsForNextRound(Battle battle) {
        UUID baseId = battle.getBaseId();

        /* TODO Remove hardcoded url */
        /* Get current units sit in the base from the base-manager module */
        String url = "http://localhost:8082/api/base/" + baseId + "/getUnitsForNextRound";
        return restTemplate.getForEntity(url, BattleNewUnitsForNextRoundDTO.class).getBody();
    }

    private void returnSupportArmiesAfterBattle(UUID baseId, BaseUnitsDTO baseUnits) {
        /* TODO Remove hardcoded url */
        /* Get defense information for this base from the base-manager module */
        String url = "http://localhost:8082/api/base/" + baseId + "/returnSupportArmiesAfterBattle";
        restTemplate.postForObject(url, baseUnits, BaseDefenseInformationDTO.class);
    }
}
