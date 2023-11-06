package org.example.controllers;

import org.example.config.WorldConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/base")
@EnableConfigurationProperties(WorldConfig.class)
public class BaseController {

    private final WorldConfig worldConfig;

    public BaseController(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    @GetMapping("HelloWorld")
    public ResponseEntity<Double> refreshToken() {
        System.out.println("TESTE");
        Double BASE = worldConfig.getBASE();
        System.out.println("worldConfig = " + worldConfig);
        System.out.println("BASE = " + BASE);

        return ResponseEntity.ok(BASE);
    }
}
