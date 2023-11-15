package org.example.services;

import org.example.interfaces.BaseIdInterface;
import org.example.exceptions.ResourceNotFoundException;
import org.example.models.Base;
import org.example.models.Player;
import org.example.repositories.BaseRepository;
import org.example.utils.Constants;
import org.example.utils.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class BaseService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);

    private final BaseRepository baseRepository;

    private final BuildingService buildingService;

    private final ResourcesUtils resourcesUtils;

    public BaseService(BaseRepository baseRepository, BuildingService buildingService, ResourcesUtils resourcesUtils) {
        this.baseRepository = baseRepository;
        this.buildingService = buildingService;
        this.resourcesUtils = resourcesUtils;
    }

    public void generateBase(Player player) {
        Map<String, Double> resources = resourcesUtils.generateDefaultResourcesForBase();

        Base base = Base.builder()
                .name("Default Name")
                .x_coordinate(new Random().nextInt(1000))
                .y_coordinate(new Random().nextInt(1000))
                .player(player)
                .score(1)
                .resources(resources)
                .lastResourcesUpdate(Timestamp.from(Instant.now()))
                .build();

        baseRepository.save(base);

        LOG.info("Created base = {}", base);

        buildingService.generateDefaultBuildingsForNewBase(base);
    }

    @Transactional
    public Base findById(UUID id) {
        Optional<Base> base = baseRepository.findById(id);

        if (base.isEmpty()) {
            throw new ResourceNotFoundException(String.format(
                    Constants.BASE_NOT_FOUND, id));
        }

        resourcesUtils.updateBaseResources(base.get());
        return base.get();
    }

    public List<BaseIdInterface> findByAllPlayerId(UUID playerId) {
        return baseRepository.findAllByPlayerId(playerId);
    }

}
