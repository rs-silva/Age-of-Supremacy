package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnitsRecruitmentEventDTO {

    private UUID baseId;

    private Map<String, Integer> units;

    private Timestamp completionTime;

}
