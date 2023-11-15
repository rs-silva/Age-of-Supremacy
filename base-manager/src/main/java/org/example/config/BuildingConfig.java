package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "buildings")
@PropertySource(value = "file:../buildings-upgrades-config.json")
public class BuildingConfig {

    private List<BuildingUpgradeConfig> buildings;

}
