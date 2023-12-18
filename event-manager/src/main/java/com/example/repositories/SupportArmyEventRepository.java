package com.example.repositories;

import com.example.models.SupportArmyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface SupportArmyEventRepository extends JpaRepository<SupportArmyEvent, UUID> {

    List<SupportArmyEvent> findByCompletionTimeBefore(Timestamp timestamp);

}

