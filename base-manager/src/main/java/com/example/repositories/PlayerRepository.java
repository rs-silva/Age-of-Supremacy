package com.example.repositories;

import com.example.interfaces.PlayerIdInterface;
import com.example.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Player findByUsername(String username);

    @Query("SELECT p FROM Player p WHERE p.id = ?1")
    PlayerIdInterface findByPlayerId(UUID playerId);

}
