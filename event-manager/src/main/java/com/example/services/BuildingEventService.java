package com.example.services;

import com.example.dto.BuildingUpgradeEventDTO;
import com.example.mappers.BuildingEventMapper;
import com.example.repositories.BuildingEventRepository;
import org.springframework.stereotype.Service;

@Service
public class BuildingEventService {

    private final BuildingEventRepository buildingEventRepository;

    public BuildingEventService(BuildingEventRepository buildingEventRepository) {
        this.buildingEventRepository = buildingEventRepository;
    }

    public void registerEvent(BuildingUpgradeEventDTO buildingUpgradeEventDTO) {
        buildingEventRepository.save(BuildingEventMapper.fromDtoToEntity(buildingUpgradeEventDTO));
    }

}
