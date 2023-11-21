package com.example.controllers;

import com.example.interfaces.PlayerIdInterface;
import com.example.models.Player;
import jakarta.validation.Valid;
import com.example.dto.ListOfBasesDTO;
import com.example.dto.NewPlayerDTO;
import com.example.interfaces.BaseIdInterface;
import com.example.mappers.PlayerMapper;
import com.example.services.PlayerService;
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

    @GetMapping("{playerId}")
    public ResponseEntity<PlayerIdInterface> getPlayer(@PathVariable UUID playerId) {

        PlayerIdInterface player = playerService.findPlayer(playerId);

        return ResponseEntity.ok(player);
    }

    @GetMapping("/listOfBases/{playerId}")
    public ResponseEntity<ListOfBasesDTO> getListOfBases(@PathVariable UUID playerId) {

        List<BaseIdInterface> baseList = playerService.getListOfBases(playerId);

        return ResponseEntity.ok(PlayerMapper.fromEntityToListOfBasesIds(playerId, baseList));
    }

}
