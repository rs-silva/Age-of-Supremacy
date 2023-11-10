package org.example.mappers;

import org.example.dto.NewPlayerDTO;
import org.example.models.Player;

public abstract class PlayerMapper {

    public static NewPlayerDTO fromEntityToNewPlayerDTO(Player player) {
        return NewPlayerDTO.builder()
                .id(player.getId().toString())
                .username(player.getUsername())
                .build();
    }

}
