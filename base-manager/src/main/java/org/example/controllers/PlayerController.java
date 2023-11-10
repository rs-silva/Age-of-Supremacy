package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dto.ListOfBasesDTO;
import org.example.dto.NewPlayerDTO;
import org.example.interfaces.BaseIdInterface;
import org.example.mappers.PlayerMapper;
import org.example.models.Player;
import org.example.services.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("api/player")
public class PlayerController {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("create")
    public ResponseEntity<NewPlayerDTO> createPlayer(@Valid @RequestBody NewPlayerDTO playerDTO) {

        LOG.info("Creating player with id {} and username {}", playerDTO.getId(), playerDTO.getUsername());

        Player player = playerService.createPlayer(playerDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(PlayerMapper.fromEntityToNewPlayerDTO(player));
    }

    @GetMapping("/listOfBases/{playerId}")
    public ResponseEntity<ListOfBasesDTO> getListOfBases(@PathVariable UUID playerId) {

        List<BaseIdInterface> baseList = playerService.getListOfBases(playerId);

        return ResponseEntity.ok(PlayerMapper.fromEntityToListOfBasesIds(playerId, baseList));
    }

}
