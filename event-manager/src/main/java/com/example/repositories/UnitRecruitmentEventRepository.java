package com.example.repositories;

import com.example.models.UnitRecruitmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface UnitRecruitmentEventRepository extends JpaRepository<UnitRecruitmentEvent, UUID> {

    List<UnitRecruitmentEvent> findByCompletionTimeBefore(Timestamp timestamp);

    List<UnitRecruitmentEvent> findByBaseId(UUID baseId);

}

