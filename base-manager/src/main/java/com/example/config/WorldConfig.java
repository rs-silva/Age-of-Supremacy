package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "resources")
@PropertySource(value = "file:../world-config.yml", factory = YamlPropertySourceFactory.class)
public class WorldConfig {

    /* Resource Production */
    private Double WORLD_GROWING_FACTOR;

    private Double RESOURCES_BASE;

    private Double RESOURCES_EXPONENTIAL;

    private Double RESOURCES_DEFAULT_AMOUNT;

    private Double WAREHOUSE_BASE;

    private Double WAREHOUSE_EXPONENTIAL;
}
