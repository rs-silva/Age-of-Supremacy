package org.example;

import org.example.utils.ResourceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CityManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CityManagerApplication.class, args);
        System.out.println("CityManagerApplication is online!\n");
        System.out.println("level 30 = " + ResourceUtils.getAmountOfResourcesProducedForLevel(30));

    }
}