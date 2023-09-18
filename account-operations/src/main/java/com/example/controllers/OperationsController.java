package com.example.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/operations")
public class OperationsController {

    private static final Logger LOG = LoggerFactory.getLogger(OperationsController.class);

    @GetMapping
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("ok");
    }

}
