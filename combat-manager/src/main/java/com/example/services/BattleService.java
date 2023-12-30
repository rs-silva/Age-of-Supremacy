package com.example.services;

import com.example.repositories.BattleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("combat-manager microservice")
public class BattleService {

    private static final Logger LOG = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;

    public BattleService(BattleRepository battleRepository) {
        this.battleRepository = battleRepository;
    }

    public findById(UUID )
}
