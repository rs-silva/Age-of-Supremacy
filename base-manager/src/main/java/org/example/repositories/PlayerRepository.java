package org.example.repositories;

import org.example.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Player findByUsername(String username);

}
