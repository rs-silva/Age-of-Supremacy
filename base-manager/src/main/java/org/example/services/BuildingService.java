package org.example.services;

import org.example.models.Base;
import org.example.models.Building;
import org.example.repositories.BuildingRepository;
import org.example.utils.BuildingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public void generateDefaultBuildingsForNewBase(Base base) {
        List<Building> buildingList = BuildingUtils.generateBuildingListForNewBase();

        for (Building building : buildingList) {
            building.setBase(base);
            buildingRepository.save(building);
        }

    }


}
