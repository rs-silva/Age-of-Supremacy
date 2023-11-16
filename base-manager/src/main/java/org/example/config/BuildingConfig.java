package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("buildings")
    private List<BuildingUpgradeConfig> buildings;

}
