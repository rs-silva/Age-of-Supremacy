package org.example;

import org.example.enums.BuildingNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CityManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CityManagerApplication.class, args);
        System.out.println("CityManagerApplication is online!\n");
        System.out.println("name = " + BuildingNames.AIRCRAFT_FACTORY.getLabel());
    }
}