package com.example.repositories;

import com.example.models.BuildingGenerationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface BuildingGenerationEventRepository extends JpaRepository<BuildingGenerationEvent, UUID> {

    List<BuildingGenerationEvent> findByCompletionTimeBefore(Timestamp timeStamp);

}

