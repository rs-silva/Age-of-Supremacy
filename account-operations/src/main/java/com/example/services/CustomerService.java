package com.example.services;

import com.example.exceptions.InternalServerErrorException;
import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.Customer;
import com.example.repositories.CustomerRepository;
import com.example.utils.AccountOperationsConstants;
import com.example.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        Customer customer = findCustomerByEmail(newCustomer.getEmail());

        if (customer != null) {
            throw new ResourceAlreadyExistsException(
                    String.format(AccountOperationsConstants.CUSTOMER_ALREADY_EXISTS_EXCEPTION, newCustomer.getEmail()));
        }

        validateTokenEmail(newCustomer.getEmail());

        customerRepository.save(newCustomer);
        LOG.info("Added Customer {} to database!", newCustomer.getEmail());
    }

    public void updateCustomerInDatabase(Customer updatedCustomer) {
        Customer customer = findCustomerByEmail(updatedCustomer.getEmail());

        if (customer == null) {
            throw new ResourceNotFoundException(
                    String.format(AccountOperationsConstants.CUSTOMER_NOT_FOUND_EXCEPTION, updatedCustomer.getEmail()));
        }

        validateTokenEmail(updatedCustomer.getEmail());

        customerRepository.save(updatedCustomer);
        LOG.info("Updated Customer {} in database!", updatedCustomer.getEmail());
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    private void validateTokenEmail(String email) {
        String emailFromToken = jwtTokenUtils.retrieveEmailFromRequestToken();

        if (!emailFromToken.equals(email)) {
            LOG.error("Email from Customer request = " + email + " | Email from token = " + emailFromToken);
            throw new InternalServerErrorException(
                    String.format(AccountOperationsConstants.CUSTOMER_EMAIL_DIFFERENT_FROM_TOKEN_EMAIL));
        }

    }
}
