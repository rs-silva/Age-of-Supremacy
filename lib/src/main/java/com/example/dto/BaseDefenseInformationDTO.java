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

    private int groundDefensePower;

    private int armoredDefensePower;

    private int airDefensePower;

    private int defenseHealthPoints;

}
