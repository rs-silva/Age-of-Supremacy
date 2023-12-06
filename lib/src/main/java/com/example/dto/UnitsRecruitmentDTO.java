package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnitsRecruitmentDTO {

    private int infantry;

    private int engineer;

    private int sniper;

    private int armoredPersonnelCarrier;

    private int mainBattleTank;

    private int artillery;

    private int jetFighter;

    private int bomber;

    private int recon;

}
