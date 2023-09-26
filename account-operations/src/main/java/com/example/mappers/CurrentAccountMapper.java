package com.example.mappers;

import com.example.dto.CurrentAccountBalanceDTO;
import com.example.dto.CurrentAccountNumberDTO;
import com.example.models.CurrentAccount;

public abstract class CurrentAccountMapper {

    public static CurrentAccountBalanceDTO fromEntityToCurrentAccountBalanceDTO(CurrentAccount currentAccount) {
        return CurrentAccountBalanceDTO.builder()
                .accountNumber(currentAccount.getAccountNumber())
                .balance(currentAccount.getBalance())
                .build();
    }

    public static CurrentAccountNumberDTO fromEntityToCurrentAccountNumberDTO(CurrentAccount currentAccount) {
        return CurrentAccountNumberDTO.builder()
                .accountNumber(currentAccount.getAccountNumber())
                .build();
    }

}
