package com.example.mappers;

import com.example.dto.BaseDTO;
import com.example.dto.BuildingDTO;
import com.example.models.Base;

import java.util.List;

public abstract class BaseMapper {

    public static BaseDTO buildDTO(Base base, List<BuildingDTO> buildingDTOList) {
        return BaseDTO
                .builder()
                .id(base.getId())
                .name(base.getName())
                .x_coordinate(base.getX_coordinate())
                .y_coordinate(base.getY_coordinate())
                .score(base.getScore())
                .resources(base.getResources())
                .units(base.getUnits())
                .supportUnits(base.getSupportUnits())
                .buildingList(buildingDTOList)
                .build();
    }

}
