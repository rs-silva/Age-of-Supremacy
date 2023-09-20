package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountOperationsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountOperationsApplication.class, args);
        System.out.println("AccountOperationsApplication is online!");
    }
}