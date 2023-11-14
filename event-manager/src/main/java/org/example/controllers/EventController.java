package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dto.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/event")
public class EventController {

    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

    @PostMapping
    public ResponseEntity<EventDTO> registerUser() {
        //LOG.info("Registering event = {}", event);

        return ResponseEntity.status(HttpStatus.CREATED).body(new EventDTO("Hello World"));
    }

}
