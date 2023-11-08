package org.example.services;

import org.example.models.Base;
import org.example.models.Building;
import org.example.models.Player;
import org.example.repositories.BaseRepository;
import org.example.utils.BaseUtils;
import org.example.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class BaseService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);

    private final BaseRepository baseRepository;

    private final BuildingService buildingService;

    public BaseService(BaseRepository baseRepository, BuildingService buildingService) {
        this.baseRepository = baseRepository;
        this.buildingService = buildingService;
    }

    public void generateBase(Player player) {
        Map<String, Integer> resources = ResourceUtils.generateDefaultResourcesForBase();

        Base base = Base.builder()
                .name("Default Name")
                .x_coordinate(new Random().nextInt(1000))
                .y_coordinate(new Random().nextInt(1000))
                .player(player)
                .resources(resources)
                .score(1)
                .build();

        Base databaseBase = baseRepository.save(base);

        buildingService.generateDefaultBuildingsForNewBase(databaseBase);
    }

}
