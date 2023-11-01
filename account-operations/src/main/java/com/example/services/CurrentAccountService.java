package com.example.services;

import com.example.dto.CurrentAccountBalanceDTO;
import com.example.dto.CurrentAccountNumberDTO;
import com.example.exceptions.BadRequestException;
import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.mappers.CurrentAccountMapper;
import com.example.models.CurrentAccount;
import com.example.models.Customer;
import com.example.repositories.CurrentAccountRepository;
import com.example.utils.AccountOperationsConstants;
import com.example.utils.JwtAccessTokenUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Service
public class CurrentAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountService.class);

    private final CurrentAccountRepository currentAccountRepository;

    private final CustomerService customerService;

    private final JwtAccessTokenUtils jwtTokenUtils;

    public CurrentAccountService(CurrentAccountRepository currentAccountRepository, CustomerService customerService, JwtAccessTokenUtils jwtTokenUtils) {
        this.currentAccountRepository = currentAccountRepository;
        this.customerService = customerService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public CurrentAccountNumberDTO createCurrentAccountForLoggedInCustomer() {
        String customerEmail = jwtTokenUtils.retrieveEmailFromRequestToken();
        Customer customer = customerService.findCustomerByEmail(customerEmail);

        if (customer.getCurrentAccount() != null) {
            throw new ResourceAlreadyExistsException(
                    String.format(AccountOperationsConstants.CUSTOMER_ALREADY_HAS_CURRENT_ACCOUNT, customerEmail));
        }

        CurrentAccount currentAccount = createCurrentAccount(customer);
        LOG.info("Created Current Account for user {}", customer.getEmail());
        return CurrentAccountMapper.fromEntityToCurrentAccountNumberDTO(currentAccount);
    }

    @Transactional
    public void depositFunds(String accountNumber, BigDecimal amount) {
        CurrentAccount currentAccount = retrieveCurrentAccountForLoggedInCustomer();
        validateAccountNumber(currentAccount, accountNumber);
        BigDecimal currentBalance = currentAccount.getBalance();
        currentAccount.setBalance(currentBalance.add(amount));
        currentAccountRepository.save(currentAccount);
        LOG.info("Deposited {} to current account with number {}!", amount, accountNumber);
    }

    @Transactional
    public void withdrawFunds(String accountNumber, BigDecimal amount) {
        CurrentAccount currentAccount = retrieveCurrentAccountForLoggedInCustomer();
        validateAccountNumber(currentAccount, accountNumber);
        BigDecimal currentBalance = currentAccount.getBalance();
        currentAccount.setBalance(currentBalance.subtract(amount));

        if (currentAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(AccountOperationsConstants.INSUFFICIENT_FUNDS_TO_WITHDRAW);
        }

        currentAccountRepository.save(currentAccount);
        LOG.info("Withdrew {} from current account with number {}!", amount, accountNumber);
    }

    public CurrentAccountNumberDTO getAccountNumber(String email) {
        customerService.validateTokenEmail(email);
        CurrentAccount currentAccount = retrieveCurrentAccountForLoggedInCustomer();
        return CurrentAccountMapper.fromEntityToCurrentAccountNumberDTO(currentAccount);
    }

    public CurrentAccountBalanceDTO getBalance(String accountNumber) {
        CurrentAccount currentAccount = retrieveCurrentAccountForLoggedInCustomer();
        validateAccountNumber(currentAccount, accountNumber);
        return CurrentAccountMapper.fromEntityToCurrentAccountBalanceDTO(currentAccount);
    }

    private CurrentAccount createCurrentAccount(Customer customer) {
        CurrentAccount currentAccount = CurrentAccount.builder()
                .accountNumber(generateAccountNumber())
                .balance(new BigDecimal(0))
                .openedDate(new Date())
                .customer(customer)
                .build();

        currentAccountRepository.save(currentAccount);

        customer.setCurrentAccount(currentAccount);
        customerService.updateCustomerInDatabase(customer);

        return currentAccount;
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString();
    }

    private CurrentAccount retrieveCurrentAccountForLoggedInCustomer() {
        String customerEmail = jwtTokenUtils.retrieveEmailFromRequestToken();
        Customer customer = customerService.findCustomerByEmail(customerEmail);
        CurrentAccount currentAccount = customer.getCurrentAccount();

        if (currentAccount == null) {
            throw new ResourceNotFoundException(
                    String.format(AccountOperationsConstants.CUSTOMER_DOES_NOT_HAVE_CURRENT_ACCOUNT, customerEmail));
        }

        return customer.getCurrentAccount();
    }

    private void validateAccountNumber(CurrentAccount currentAccount, String accountNumber) {
        boolean isAccountNumberValid = currentAccount.getAccountNumber().equals(accountNumber);
        if (!isAccountNumberValid) {
            throw new BadRequestException(AccountOperationsConstants.WRONG_CURRENT_ACCOUNT_NUMBER);
        }
    }

}
