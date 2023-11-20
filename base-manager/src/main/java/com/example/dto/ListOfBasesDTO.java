package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.example.interfaces.BaseIdInterface;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ListOfBasesDTO {

    private String playerId;

    private List<BaseIdInterface> baseList;

}
