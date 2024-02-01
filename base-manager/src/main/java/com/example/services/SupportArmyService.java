package com.example.services;

import com.example.dto.ArmySimpleDTO;
import com.example.exceptions.BadRequestException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.SupportArmy;
import com.example.repositories.SupportArmyRepository;
import com.example.utils.ArmyUtils;
import com.example.utils.BaseManagerConstants;
import com.example.utils.SupportArmyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public void createSupportArmySendRequest(UUID originBaseId, UUID destinationBaseId, ArmySimpleDTO armySimpleDTO) {
        if (originBaseId.equals(destinationBaseId)) {
            throw new BadRequestException(BaseManagerConstants.SUPPORT_ARMY_ORIGIN_BASE_AND_DESTINATION_BASE_ARE_EQUAL);
        }

        Base originBase = baseService.findById(originBaseId);
        baseService.validateBaseOwnership(originBase.getPlayer().getId());

        Base destinationBase = baseService.findById(destinationBaseId);

        supportArmyUtils.createSupportArmySendRequest(originBase, destinationBase, armySimpleDTO);
    }

    @Transactional
    public void completeSupportArmySendRequest(UUID originBaseId, UUID destinationBaseId, ArmySimpleDTO armySimpleDTO) {
        Base destinationBase = baseService.findById(destinationBaseId);
        List<SupportArmy> destinationBaseCurrentSupportArmyList = destinationBase.getSupportArmies();
        Map<String, Integer> newSupportUnits = armySimpleDTO.getUnits();

        SupportArmy supportArmy = findByOwnerBaseId(destinationBaseCurrentSupportArmyList, originBaseId);

        /* If a support army from the origin base does not exist, create a new one */
        if (supportArmy == null) {
            SupportArmy newSupportArmy = SupportArmy.builder()
                    .ownerBaseId(originBaseId)
                    .baseBeingSupported(destinationBase)
                    .units(newSupportUnits)
                    .build();

            supportArmyRepository.save(newSupportArmy);
        }
        /* If there's already a support army from the origin base, add the units from this new request */
        else {
            Map<String, Integer> destinationBaseCurrentSupportArmyUnits = supportArmy.getUnits();
            Map<String, Integer> updatedArmy = ArmyUtils.addUnitsToArmy(destinationBaseCurrentSupportArmyUnits, newSupportUnits);
            supportArmy.setUnits(updatedArmy);
        }
    }

    @Transactional
    public void createSupportArmyReturnRequest(UUID supportArmyId, ArmySimpleDTO armySimpleDTO) {
        SupportArmy supportArmy = findById(supportArmyId);
        Base ownerBase = baseService.findById(supportArmy.getOwnerBaseId());

        baseService.validateBaseOwnership(ownerBase.getPlayer().getId());

        supportArmyUtils.createSupportArmyReturnRequest(ownerBase, supportArmy, armySimpleDTO);

        boolean isSupportArmyEmpty = ArmyUtils.isArmyEmpty(supportArmy.getUnits());

        if (isSupportArmyEmpty) {
            supportArmyRepository.delete(supportArmy);
        }
    }

    @Transactional
    public void completeSupportArmyReturnRequest(UUID ownerBaseId, ArmySimpleDTO armySimpleDTO) {
        Base ownerBase = baseService.findById(ownerBaseId);

        Map<String, Integer> updatedArmy = ArmyUtils.addUnitsToArmy(ownerBase.getUnits(), armySimpleDTO.getUnits());
        ownerBase.setUnits(updatedArmy);
    }

    public SupportArmy findById(UUID id) {
        Optional<SupportArmy> supportArmy = supportArmyRepository.findById(id);

        if (supportArmy.isEmpty()) {
            throw new ResourceNotFoundException(String.format(
                    BaseManagerConstants.SUPPORT_ARMY_NOT_FOUND, id));
        }

        return supportArmy.get();
    }

    private SupportArmy findByOwnerBaseId(List<SupportArmy> supportArmyList, UUID ownerBaseId) {
        for (SupportArmy supportArmy : supportArmyList) {
            if (supportArmy.getOwnerBaseId().equals(ownerBaseId)) {
                return supportArmy;
            }
        }

        return null;
    }



    public void deleteAll(List<SupportArmy> supportArmies) {
        supportArmyRepository.deleteAll(supportArmies);
    }

}
