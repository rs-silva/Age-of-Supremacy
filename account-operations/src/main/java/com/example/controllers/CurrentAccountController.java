package com.example.controllers;

import com.example.dto.CurrentAccountBalanceDTO;
import com.example.dto.CurrentAccountNumberDTO;
import com.example.services.CurrentAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/current_account")
public class CurrentAccountController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountController.class);

    private final CurrentAccountService currentAccountService;

    public CurrentAccountController(CurrentAccountService currentAccountService) {
        this.currentAccountService = currentAccountService;
    }

    @PostMapping()
    public ResponseEntity<CurrentAccountNumberDTO> createCurrentAccount() {
        CurrentAccountNumberDTO currentAccountNumberDTO = currentAccountService.createCurrentAccountForLoggedInCustomer();
        return ResponseEntity.ok(currentAccountNumberDTO);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositFunds(@RequestParam String accountNumber,
                                               @RequestParam BigDecimal amount) {
        currentAccountService.depositFunds(accountNumber, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawFunds(@RequestParam String accountNumber,
                                                @RequestParam BigDecimal amount) {
        currentAccountService.withdrawFunds(accountNumber, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/accountNumber")
    public ResponseEntity<CurrentAccountNumberDTO> getAccountNumber(@RequestParam String email) {
        CurrentAccountNumberDTO accountNumber = currentAccountService.getAccountNumber(email);
        return ResponseEntity.ok(accountNumber);
    }

    @GetMapping("/balance")
    public ResponseEntity<CurrentAccountBalanceDTO> getBalance(@RequestParam String accountNumber) {
        CurrentAccountBalanceDTO balance = currentAccountService.getBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

}
