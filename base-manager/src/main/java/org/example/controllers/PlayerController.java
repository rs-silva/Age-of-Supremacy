package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dto.NewPlayerDTO;
import org.example.models.Player;
import org.example.services.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/player")
public class PlayerController {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("create")
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody NewPlayerDTO playerDTO) {

        LOG.info("Creating player with id {} and username {}", playerDTO.getId(), playerDTO.getUsername());

        Player player = playerService.createPlayer(playerDTO);

        return ResponseEntity.ok(player);
    }

    @GetMapping("listOfBases")
    public ResponseEntity<Player> getListOfBases() {

        //LOG.info("Retrieving player with id {} and username {}", playerDTO.getId(), playerDTO.getUsername());

        //Player player = playerService.createPlayer(playerDTO);

        return ResponseEntity.ok(new Player());
    }
}
