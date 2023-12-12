package com.example.repositories;

import com.example.models.BuildingUpgradeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface BuildingUpgradeEventRepository extends JpaRepository<BuildingUpgradeEvent, UUID> {

    List<BuildingUpgradeEvent> findByCompletionTimeBefore(Timestamp timestamp);

    BuildingUpgradeEvent findByBuildingId(UUID buildingId);

}

