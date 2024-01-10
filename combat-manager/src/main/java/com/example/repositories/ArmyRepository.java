package com.example.repositories;

import com.example.enums.ArmyRole;
import com.example.models.Army;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArmyRepository extends JpaRepository<Army, UUID> {

    List<Army> findByBattleIdAndRole(UUID battleId, ArmyRole armyRole);

    Army findByBattleIdAndOwnerBaseId(UUID battleId, UUID ownerBaseId);

}

