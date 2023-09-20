package com.example.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/accounts")
public class AccountsController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);

    @GetMapping
    public ResponseEntity<String> createAccount() {
        LOG.info("TESTE");
        return ResponseEntity.ok("ok");
    }

}
