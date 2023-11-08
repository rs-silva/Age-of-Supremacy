package org.example.services;

import org.example.dto.NewPlayerDTO;
import org.example.exceptions.ResourceAlreadyExistsException;
import org.example.models.Player;
import org.example.repositories.PlayerRepository;
import org.example.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final BaseService baseService;

    public PlayerService(PlayerRepository playerRepository, BaseService baseService) {
        this.playerRepository = playerRepository;
        this.baseService = baseService;
    }

    public Player createPlayer(NewPlayerDTO playerDTO) {
        UUID uuid = UUID.fromString(playerDTO.getId());
        checkIfPlayerAlreadyExists(uuid, playerDTO.getUsername());

        Player player = Player.builder()
                .id(uuid)
                .username(playerDTO.getUsername())
                .totalScore(0)
                .build();
        playerRepository.save(player);

        baseService.generateBase(player);

        return player;
    }

    private void checkIfPlayerAlreadyExists(UUID playerId, String username) {
        Optional<Player> playerById = playerRepository.findById(playerId);
        Player playerByUsername = playerRepository.findByUsername(username);

        if (playerById.isPresent() || playerByUsername != null) {
            throw new ResourceAlreadyExistsException(String.format(
                    Constants.PLAYER_ALREADY_EXISTS, playerId, username));
        }

    }

}
