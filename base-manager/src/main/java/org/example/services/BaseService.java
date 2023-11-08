package org.example.services;

import org.example.models.Base;
import org.example.models.Player;
import org.example.repositories.BaseRepository;
import org.example.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
public class BaseService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);

    private final BaseRepository baseRepository;

    private final BuildingService buildingService;

    private final ResourceUtils resourceUtils;

    public BaseService(BaseRepository baseRepository, BuildingService buildingService, ResourceUtils resourceUtils) {
        this.baseRepository = baseRepository;
        this.buildingService = buildingService;
        this.resourceUtils = resourceUtils;
    }

    public void generateBase(Player player) {
        Map<String, Integer> resources = resourceUtils.generateDefaultResourcesForBase();

        Base base = Base.builder()
                .name("Default Name")
                .x_coordinate(new Random().nextInt(1000))
                .y_coordinate(new Random().nextInt(1000))
                .player(player)
                .resources(resources)
                .score(1)
                .build();

        Base databaseBase = baseRepository.save(base);

        LOG.info("Created base = {}", base);

        buildingService.generateDefaultBuildingsForNewBase(databaseBase);
    }

}
