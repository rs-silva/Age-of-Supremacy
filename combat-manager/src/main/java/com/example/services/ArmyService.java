package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.enums.ArmyRole;
import com.example.models.Army;
import com.example.models.Battle;
import com.example.repositories.ArmyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Army> findByBattleId(UUID battleId) {
        return armyRepository.findByBattleId(battleId);
    }

    public void generateAttackingArmy(UUID ownerPlayerId, UUID originBaseId, ArmyDTO armyDTO, Battle battle) {
        Army army = Army.builder()
                .ownerPlayerId(ownerPlayerId)
                .ownerBaseId(originBaseId)
                .role(ArmyRole.ATTACKING)
                .battle(battle)
                .units(armyDTO.getUnits())
                .build();

        armyRepository.save(army);
    }

    public void generateDefendingArmy(UUID ownerPlayerId, UUID originBaseId, ArmyDTO armyDTO) {
        Army army = Army.builder()
                .ownerPlayerId(ownerPlayerId)
                .ownerBaseId(originBaseId)
                .role(ArmyRole.DEFENDING)
                .units(armyDTO.getUnits())
                .build();

        armyRepository.save(army);
    }
}
