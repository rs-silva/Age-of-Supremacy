package com.example.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CurrentAccountBalanceDTO {

    private String accountNumber;

    private BigDecimal balance;

}
