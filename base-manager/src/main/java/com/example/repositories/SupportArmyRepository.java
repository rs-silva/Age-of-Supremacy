package com.example.repositories;

import com.example.models.SupportArmy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupportArmyRepository extends JpaRepository<SupportArmy, UUID> {

    SupportArmy findByOwnerBaseId(UUID ownerBaseId);

}

