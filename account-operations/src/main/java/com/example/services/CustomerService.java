package com.example.services;

import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.models.Customer;
import com.example.repositories.CustomerRepository;
import com.example.utils.AccountOperationsConstants;
import com.example.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final JwtTokenUtils jwtTokenUtils;

    public CustomerService(CustomerRepository customerRepository, JwtTokenUtils jwtTokenUtils) {
        this.customerRepository = customerRepository;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public void addCustomerToDatabase(Customer newCustomer) {
        Customer customer = customerRepository.findByEmail(newCustomer.getEmail());

        if (customer != null) {
            throw new ResourceAlreadyExistsException(
                    String.format(AccountOperationsConstants.CUSTOMER_ALREADY_EXISTS_EXCEPTION, newCustomer.getEmail()));
        }

        String token = jwtTokenUtils.retrieveTokenFromRequest();
        String emailFromToken = jwtTokenUtils.getEmailFromToken(token);

        if (!emailFromToken.equals(newCustomer.getEmail())) {
            LOG.error("Email from Customer request = " + newCustomer.getEmail() + " | Email from token = " + emailFromToken);
            throw new InternalServerErrorException(
                    String.format(AccountOperationsConstants.CUSTOMER_EMAIL_DIFFERENT_FROM_TOKEN_EMAIL));
        }

        customerRepository.save(newCustomer);
        LOG.info("Added Customer {} to database!", newCustomer.getEmail());
    }
}
