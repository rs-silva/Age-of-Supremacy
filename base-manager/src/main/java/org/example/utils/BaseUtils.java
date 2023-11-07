package org.example.utils;

import org.example.enums.BuildingNames;
import org.example.models.Building;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseUtils {

    public static List<Building> generateBuildingListForNewBase() {
        List<Building> buildingList = new ArrayList<>();

        /* Resource Buildings */
        Building resource1Producer = BuildingUtils.generateNewResourceBuilding(BuildingNames.RESOURCE_1_FACTORY.getLabel());
        buildingList.add(resource1Producer);

        Building resource2Producer = BuildingUtils.generateNewResourceBuilding(BuildingNames.RESOURCE_2_FACTORY.getLabel());
        buildingList.add(resource2Producer);

        return buildingList;
    }

}
