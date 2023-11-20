package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BaseManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseManagerApplication.class, args);
        System.out.println("BaseManagerApplication is online!\n");
    }
}