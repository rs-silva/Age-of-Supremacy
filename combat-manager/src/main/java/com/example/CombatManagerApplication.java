package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CombatManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CombatManagerApplication.class, args);
        System.out.println("CombatManagerApplication is online!\n");
    }
}