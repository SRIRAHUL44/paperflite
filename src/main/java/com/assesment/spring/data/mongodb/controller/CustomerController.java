package com.assesment.spring.data.mongodb.controller;

import com.assesment.spring.data.mongodb.model.AccountEntity;
import com.assesment.spring.data.mongodb.model.CustomerEntity;
import com.assesment.spring.data.mongodb.repository.AccountRepository;
import com.assesment.spring.data.mongodb.repository.CustomerRepository; // Assuming you create a CustomerRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;


    @GetMapping("/customers/account/{accountId}")
    public ResponseEntity<Page<CustomerEntity>> getCustomersByAccountId(
            @PathVariable("accountId") String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        Page<CustomerEntity> customersPage = customerRepository.findByAccountId_Id(accountId, pageable);

        return customersPage.hasContent()
                ? new ResponseEntity<>(customersPage, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/customers")
    public ResponseEntity<CustomerEntity> createCustomer(@RequestBody CustomerEntity customerEntity) {
        try {
            if (customerEntity.getAccountId() == null || customerEntity.getAccountId().getId() == null) {
                logger.error("Account ID must be provided.");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Optional<AccountEntity> accountOpt = accountRepository.findById(customerEntity.getAccountId().getId());

            if (!accountOpt.isPresent()) {
                logger.error("Account not found with ID: {}", customerEntity.getAccountId().getId());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            customerEntity.setAccountId(accountOpt.get());

            logger.info("Creating new customer: {}", customerEntity);
            CustomerEntity createdCustomer = customerRepository.save(customerEntity);
            logger.info("Customer created successfully: {}", createdCustomer);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating customer: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerEntity> updateCustomer(@PathVariable("id") String id, @RequestBody CustomerEntity customerEntity) {
        logger.info("Updating customer with ID: {}", id);
        Optional<CustomerEntity> existingCustomerOpt = customerRepository.findById(id);

        if (existingCustomerOpt.isPresent()) {
            if (customerEntity.getAccountId() == null || customerEntity.getAccountId().getId() == null) {
                logger.error("Account ID must be provided.");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Optional<AccountEntity> accountOpt = accountRepository.findById(customerEntity.getAccountId().getId());

            if (!accountOpt.isPresent()) {
                logger.error("Account not found with ID: {}", customerEntity.getAccountId().getId());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            customerEntity.setAccountId(accountOpt.get());
            customerEntity.setId(id);
            CustomerEntity updatedCustomer = customerRepository.save(customerEntity);
            logger.info("Customer updated successfully: {}", updatedCustomer);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } else {
            logger.warn("Customer not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
