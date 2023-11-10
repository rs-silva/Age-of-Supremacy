package org.example.services;

import org.example.dto.NewPlayerDTO;
import org.example.exceptions.ForbiddenException;
import org.example.exceptions.ResourceAlreadyExistsException;
import org.example.interfaces.BaseIdInterface;
import org.example.models.Player;
import org.example.repositories.PlayerRepository;
import org.example.utils.Constants;
import org.example.utils.JwtAccessTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final BaseService baseService;

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    public PlayerService(PlayerRepository playerRepository, BaseService baseService, JwtAccessTokenUtils jwtAccessTokenUtils) {
        this.playerRepository = playerRepository;
        this.baseService = baseService;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
    }

    public Player createPlayer(NewPlayerDTO playerDTO) {
        UUID uuid = UUID.fromString(playerDTO.getId());
        validateIdFromToken(UUID.fromString(playerDTO.getId()));
        checkIfPlayerAlreadyExists(uuid, playerDTO.getUsername());

        Player player = Player.builder()
                .id(uuid)
                .username(playerDTO.getUsername())
                .totalScore(0)
                .baseList(new ArrayList<>())
                .build();
        playerRepository.saveAndFlush(player);

        LOG.info("Created player = {}", player);

        baseService.generateBase(player);

        return player;
    }

    public List<BaseIdInterface> getListOfBases(UUID playerId) {
        validateIdFromToken(playerId);
        return baseService.findByPlayerId(playerId);
    }

    private void checkIfPlayerAlreadyExists(UUID playerId, String username) {
        Optional<Player> playerById = playerRepository.findById(playerId);
        Player playerByUsername = playerRepository.findByUsername(username);

        if (playerById.isPresent() || playerByUsername != null) {
            throw new ResourceAlreadyExistsException(String.format(
                    Constants.PLAYER_ALREADY_EXISTS, playerId, username));
        }
    }

    private void validateIdFromToken(UUID playerId) {
        UUID tokenPlayerId = jwtAccessTokenUtils.retrievePlayerIdFromToken();

        if (!playerId.equals(tokenPlayerId)) {
            LOG.error(Constants.PLAYER_ID_FROM_REQUEST_DOES_NOT_MATCH_TOKEN_PLAYER_ID +
                    " Player ID from request = {} | Player ID from Token = {}", playerId, tokenPlayerId);
            throw new ForbiddenException(
                    Constants.PLAYER_ID_FROM_REQUEST_DOES_NOT_MATCH_TOKEN_PLAYER_ID);
        }

    }

}
