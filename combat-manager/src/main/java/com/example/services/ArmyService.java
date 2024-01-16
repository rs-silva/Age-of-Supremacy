package com.example.services;

import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.ArmyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ArmyService {

    private final ArmyRepository armyRepository;

    public ArmyService(ArmyRepository armyRepository) {
        this.armyRepository = armyRepository;
    }

    public Army findByBattleIdAndOwnerBaseId(UUID battleId, UUID ownerBaseId) {
        return armyRepository.findByBattleIdAndOwnerBaseId(battleId, ownerBaseId);
    }

    public List<Army> findByBattleIdAndRole(UUID battleId, ArmyRole armyRole) {
        return armyRepository.findByBattleIdAndRole(battleId, armyRole);
    }

    public void generateAttackingArmy(UUID ownerPlayerId, UUID ownerBaseId, Map<String, Integer> units, Battle battle) {
        generateArmy(ownerPlayerId, ownerBaseId, units, battle, ArmyRole.ATTACKING);
    }

    public void generateDefendingArmy(UUID ownerPlayerId, UUID ownerBaseId, Map<String, Integer> units, Battle battle) {
        generateArmy(ownerPlayerId, ownerBaseId, units, battle, ArmyRole.DEFENDING);
    }

    private void generateArmy(UUID ownerPlayerId, UUID ownerBaseId, Map<String, Integer> units, Battle battle, ArmyRole armyRole) {
        Army army = Army.builder()
                .ownerPlayerId(ownerPlayerId)
                .ownerBaseId(ownerBaseId)
                .role(armyRole)
                .battle(battle)
                .units(units)
                .build();

        armyRepository.save(army);
    }

    public void save(Army army) {
        armyRepository.save(army);
    }
}
