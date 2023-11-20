package com.example.repositories;

import com.example.models.BuildingUpgradeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BuildingEventRepository extends JpaRepository<BuildingUpgradeEvent, UUID> {

}

