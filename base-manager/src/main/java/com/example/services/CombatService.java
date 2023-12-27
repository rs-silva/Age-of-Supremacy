package com.example.services;

import com.example.dto.ArmyDTO;
import com.example.exceptions.BadRequestException;
import com.example.models.Base;
import com.example.models.SupportArmy;
import com.example.utils.BaseManagerConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CombatService {

    private final BaseService baseService;

    public CombatService(BaseService baseService) {
        this.baseService = baseService;
    }

    @Transactional
    public void createAttackSendRequest(UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        if (originBaseId.equals(destinationBaseId)) {
            throw new BadRequestException(BaseManagerConstants.ATTACK_ORIGIN_BASE_AND_DESTINATION_BASE_ARE_EQUAL);
        }

        Base originBase = baseService.findById(originBaseId);
        baseService.validateBaseOwnership(originBase.getPlayer().getId());

        Base destinationBase = baseService.findById(destinationBaseId);

        supportArmyUtils.createSupportArmySendRequest(originBase, destinationBase, armyDTO);
    }

    @Transactional
    public void completeSupportArmySendRequest(UUID originBaseId, UUID destinationBaseId, ArmyDTO armyDTO) {
        Base destinationBase = baseService.findById(destinationBaseId);
        List<SupportArmy> destinationBaseCurrentSupportArmyList = destinationBase.getSupportArmies();
        Map<String, Integer> newSupportUnits = armyDTO.getUnits();

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
            for (String unitName : newSupportUnits.keySet()) {
                int unitAmountToAdd = newSupportUnits.get(unitName);

                if (destinationBaseCurrentSupportArmyUnits.containsKey(unitName)) {
                    int unitCurrentAmount = destinationBaseCurrentSupportArmyUnits.get(unitName);

                    int unitUpdatedAmount = unitCurrentAmount + unitAmountToAdd;

                    destinationBaseCurrentSupportArmyUnits.put(unitName, unitUpdatedAmount);
                }
                else {
                    destinationBaseCurrentSupportArmyUnits.put(unitName, unitAmountToAdd);
                }
            }
        }
    }

}
