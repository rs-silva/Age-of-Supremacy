package com.example.config;

import com.example.models.Address;
import com.example.models.Customer;
import com.example.repositories.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


@Configuration
public class AccountOperationsConfig {

    private final CustomerRepository customerRepository;

    public AccountOperationsConfig(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Profile("test")
    @Bean
    public void populateDB() {
        List<Customer> customerList = new ArrayList<>();

        Address address1 = Address.builder()
                .street("street")
                .city("Lisbon")
                .state("Lisbon")
                .postalCode("1234-567")
                .country("Portugal")
                .build();

        Customer customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("user@gmail.com")
                .dateOfBirth(new GregorianCalendar(2000, Calendar.FEBRUARY, 25).getTime())
                .gender("Male")
                .address(address1)
                .build();

        customerList.add(customer1);

        customerRepository.saveAll(customerList);

    }


}
