package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import com.example.models.SupportArmy;
import com.example.repositories.SupportArmyRepository;
import com.example.utils.BaseManagerConstants;
import com.example.utils.SupportArmyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupportArmyService {

    private final BaseService baseService;

    private final SupportArmyRepository supportArmyRepository;

    private final SupportArmyUtils supportArmyUtils;

    public SupportArmyService(BaseService baseService, SupportArmyRepository supportArmyRepository, SupportArmyUtils supportArmyUtils) {
        this.baseService = baseService;
        this.supportArmyRepository = supportArmyRepository;
        this.supportArmyUtils = supportArmyUtils;
    }

    @Transactional
    public void createSupportArmySendRequest(UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        if (originBaseId.equals(destinationBaseId)) {
            throw new BadRequestException(BaseManagerConstants.SUPPORT_ARMY_ORIGIN_BASE_AND_DESTINATION_BASE_ARE_EQUAL);
        }

        Base originBase = baseService.findById(originBaseId);
        baseService.validateBaseOwnership(originBase.getPlayer().getId());

        Base destinationBase = baseService.findById(destinationBaseId);

        supportArmyUtils.createSupportArmySendRequest(originBase, destinationBase, armyDTO);
    }

    @Transactional
    public void completeSupportArmySendRequest(UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        Base destinationBase = baseService.findById(destinationBaseId);

        SupportArmy supportArmy = findByOwnerBaseId(originBaseId);

        if (supportArmy == null) {
            SupportArmy newSupportArmy = SupportArmy.builder()
                    .ownerBaseId(originBaseId)
                    .baseBeingSupported(destinationBase)
                    .units(armyDTO.getUnits())
                    .build();

            supportArmyRepository.save(newSupportArmy);
        }
        else {

        }
    }

    private SupportArmy findByOwnerBaseId(UUID ownerBaseId) {
        return supportArmyRepository.findByOwnerBaseId(ownerBaseId);
    }
}
