package com.example.mappers;

import com.example.dto.ListOfBasesDTO;
import com.example.dto.NewPlayerDTO;
import com.example.models.Player;
import com.example.interfaces.BaseIdInterface;

import java.util.List;
import java.util.UUID;

public abstract class PlayerMapper {

    public static NewPlayerDTO fromEntityToNewPlayerDTO(Player player) {
        return NewPlayerDTO.builder()
                .id(player.getId().toString())
                .username(player.getUsername())
                .build();
    }

    public static ListOfBasesDTO fromEntityToListOfBasesIds(UUID playerId, List<BaseIdInterface> baseList) {
        return ListOfBasesDTO.builder()
                .playerId(playerId.toString())
                .baseList(baseList)
                .build();
    }

}
