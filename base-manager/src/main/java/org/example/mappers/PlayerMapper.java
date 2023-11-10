package org.example.mappers;

import org.example.dto.ListOfBasesDTO;
import org.example.dto.NewPlayerDTO;
import org.example.interfaces.BaseIdInterface;
import org.example.models.Player;

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
