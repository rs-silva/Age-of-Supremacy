package com.example.controllers;

import com.example.services.CurrentAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/current_account")
public class CurrentAccountController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountController.class);

    private final CurrentAccountService currentAccountService;

    public CurrentAccountController(CurrentAccountService currentAccountService) {
        this.currentAccountService = currentAccountService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCurrentAccount() {
        currentAccountService.createCurrentAccountForLoggedInCustomer();
        return ResponseEntity.ok().build();
    }

}
