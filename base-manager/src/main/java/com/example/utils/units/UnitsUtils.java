package com.example.utils.units;

import com.example.dto.ArmySimpleDTO;
import com.example.models.Base;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class UnitsUtils {

    public Timestamp calculateUnitsArrivalTime(Base originBase, Base destinationBase, ArmySimpleDTO armySimpleDTO) {
        /* TODO calculate travelling time based on the bases' coordinates and the units' movement speed */
        int travellingTimeInSeconds = 5;

        return Timestamp.from(Instant.now().plusMillis(travellingTimeInSeconds * 1000));
    }

}
