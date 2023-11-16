package org.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BuildingResourceConfig {

    @JsonProperty("resourceName")
    private String resourceName;

    @JsonProperty("quantity")
    private int quantity;

}
