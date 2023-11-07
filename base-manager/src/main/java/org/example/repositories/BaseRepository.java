package org.example.repositories;

import org.example.models.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BaseRepository extends JpaRepository<Base, UUID> {

}

