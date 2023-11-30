package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "")
@PropertySource(name = "Units Status Configuration", value = "file:../units-config.json", factory = JsonPropertySourceLoader.class)
public class UnitConfig {

    private List<UnitStatusConfig> units;

}
