package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BaseUnitsDTO {

    Map<String, Integer> ownUnits;

    List<ArmyExtendedDTO> supportArmies;

}
