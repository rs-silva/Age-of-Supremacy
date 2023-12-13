package com.example.utils.buildings;

import com.example.models.Base;
import com.example.models.Building;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildingUtils {

    public Building getBuilding(Base base, String buildingName) {
        List<Building> buildingList = base.getBuildings();

        for (Building building : buildingList) {
            if (building.getType().equals(buildingName)) {
                return building;
            }
        }

        return null;
    }

}
