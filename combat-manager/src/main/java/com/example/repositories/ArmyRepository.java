package com.example.repositories;

import com.example.models.Army;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArmyRepository extends JpaRepository<Army, UUID> {

}

