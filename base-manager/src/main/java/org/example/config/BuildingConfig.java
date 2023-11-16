package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "")
@PropertySource(name = "Buildings Leveling Configuration", value = "file:../buildings-leveling-config.json", factory = JsonPropertySourceLoader.class)
public class BuildingConfig {

    private List<BuildingUpgradeConfig> buildings;

}
