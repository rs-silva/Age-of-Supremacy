package com.example.repositories;

import com.example.models.AttackArmyEvent;
import com.example.models.SupportArmyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttackArmyEventRepository extends JpaRepository<AttackArmyEvent, UUID> {

    List<AttackArmyEvent> findByCompletionTimeBefore(Timestamp timestamp);

}

