package com.example.services;

import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.models.CurrentAccount;
import com.example.models.Customer;
import com.example.repositories.CurrentAccountRepository;
import com.example.utils.AccountOperationsConstants;
import com.example.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CurrentAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrentAccountService.class);

    private final CurrentAccountRepository currentAccountRepository;

    private final CustomerService customerService;

    private final JwtTokenUtils jwtTokenUtils;

    public CurrentAccountService(CurrentAccountRepository currentAccountRepository, CustomerService customerService, JwtTokenUtils jwtTokenUtils) {
        this.currentAccountRepository = currentAccountRepository;
        this.customerService = customerService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public void createCurrentAccountForLoggedInCustomer() {
        String userEmail = jwtTokenUtils.retrieveEmailFromRequestToken();
        Customer customer = customerService.findCustomerByEmail(userEmail);

        if (customer.getCurrentAccount() != null) {

            throw new ResourceAlreadyExistsException(
                    String.format(AccountOperationsConstants.CUSTOMER_ALREADY_HAS_CURRENT_ACCOUNT, userEmail));
        }

        createCurrentAccount(customer);
        LOG.info("Created Current Account for user {}", customer.getEmail());
    }

    private void createCurrentAccount(Customer customer) {
        CurrentAccount currentAccount = CurrentAccount.builder()
                .accountNumber("1")
                .balance((double) 0)
                .openedDate(new Date())
                .customer(customer)
                .build();

        currentAccountRepository.save(currentAccount);

        customer.setCurrentAccount(currentAccount);
        customerService.updateCustomerInDatabase(customer);
    }

}
