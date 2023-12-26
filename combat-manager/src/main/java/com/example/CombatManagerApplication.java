package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CombatManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CombatManagerApplication.class, args);
        System.out.println("CombatManagerApplication is online!\n");    }
}