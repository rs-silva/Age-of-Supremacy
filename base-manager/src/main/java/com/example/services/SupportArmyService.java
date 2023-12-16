package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.models.Base;
import com.example.utils.SupportArmyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupportArmyService {

    private final BaseService baseService;

    private final SupportArmyUtils supportArmyUtils;

    public SupportArmyService(BaseService baseService, SupportArmyUtils supportArmyUtils) {
        this.baseService = baseService;
        this.supportArmyUtils = supportArmyUtils;
    }

    @Transactional
    public void createSupportArmyRequest(UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        if (originBaseId.equals(destinationBaseId)) {
            throw new
        }

        Base originBase = baseService.findById(originBaseId);
        baseService.validateBaseOwnership(originBase.getPlayer().getId());

        Base destinationBase = baseService.findById(destinationBaseId);

        supportArmyUtils.createSupportArmyRequest(originBase, destinationBase, armyDTO);
    }
}
