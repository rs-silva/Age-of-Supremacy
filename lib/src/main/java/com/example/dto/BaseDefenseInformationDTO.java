package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BaseDefenseInformationDTO {

    private int groundDefense;

    private int antiTankDefense;

    private int antiAirDefense;

    private int defenseHealthPoints;

}
