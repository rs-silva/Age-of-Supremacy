package com.example.utils.battle;

import com.example.enums.UnitNames;
import com.example.models.Army;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EngagementPhaseUtils {

    public void calculateArmiesLosses(List<Army> attackingArmies, List<Army> defendingArmies) {
        /* Calculate damage for each unit type */
        for (String unitName : UnitNames.getAttackUnitsNames()) {

        }

    }

}
