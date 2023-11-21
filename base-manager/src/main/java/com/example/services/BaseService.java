package com.example.services;

import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Base;
import com.example.models.Player;
import com.example.repositories.BaseRepository;
import com.example.utils.JwtAccessTokenUtils;
import com.example.utils.ResourcesUtils;
import com.example.interfaces.BaseIdInterface;
import com.example.utils.Constants;
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

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    private final ResourcesUtils resourcesUtils;

    public BaseService(BaseRepository baseRepository, BuildingService buildingService, JwtAccessTokenUtils jwtAccessTokenUtils, ResourcesUtils resourcesUtils) {
        this.baseRepository = baseRepository;
        this.buildingService = buildingService;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
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
    public void createNewBuildingConstructionRequest(UUID baseId, String buildingType) {
        Base base = findById(baseId);

        validateBaseOwnership(base.getPlayer().getId());

        buildingService.requestBuildingGeneration(base, buildingType);
    }

    @Transactional
    public void completeBuildingGeneration(UUID baseId, String buildingType) {
        Base base = findById(baseId);

        buildingService.completeGeneration(base, buildingType);
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

    private void validateBaseOwnership(UUID basePlayerId) {
        UUID playerIdFromToken = jwtAccessTokenUtils.retrievePlayerIdFromToken();

        if (!basePlayerId.equals(playerIdFromToken)) {
            LOG.error("User with id {} attempted to perform an operation in a base that belong to {}", playerIdFromToken, basePlayerId);
            throw new InternalServerErrorException(Constants.BASE_DOES_NOT_BELONG_TO_THE_LOGGED_IN_PLAYER);
        }
    }

    public List<BaseIdInterface> findByAllPlayerId(UUID playerId) {
        return baseRepository.findAllByPlayerId(playerId);
    }

}
