package com.example.services;

import com.example.dto.UnitDTO;
import com.example.enums.UnitNames;
import com.example.exceptions.InternalServerErrorException;
import com.example.utils.BaseManagerConstants;
import com.example.utils.UnitConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnitService {

    private static final Logger LOG = LoggerFactory.getLogger(UnitService.class);

    private final UnitConfigUtils unitConfigUtils;

    public UnitService(UnitConfigUtils unitConfigUtils) {
        this.unitConfigUtils = unitConfigUtils;
    }

    public List<UnitDTO> getAllUnitsInformation() {
        List<UnitDTO> unitDTOList = new ArrayList<>();

        for (UnitNames unitName : UnitNames.values()) {
            UnitDTO unitDTO = unitConfigUtils.getUnitConfig(unitName.getLabel());
            if (unitDTO == null) {
                LOG.error("There was an error while retrieving the recruitment information for {}.", unitName.getLabel());
                throw new InternalServerErrorException(BaseManagerConstants.UNIT_CONFIG_NOT_FOUND_ERROR);
            }
            unitDTOList.add(unitDTO);
        }

        return unitDTOList;
    }

}
