package com.assesment.spring.data.mongodb.migration;

import com.assesment.spring.data.mongodb.model.CustomerEntity;
import com.assesment.spring.data.mongodb.repository.AccountRepository;
import com.assesment.spring.data.mongodb.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class CustomerMigration {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void migrateCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        for (CustomerEntity customer : customers) {
            if (customer.getAccountId() != null) {
                String accountId = customer.getAccountId().getId();
                System.out.println("Checking account ID: " + accountId + " for customer: " + customer.getFirstName());
                if (!accountRepository.existsById(accountId)) {
                    customerRepository.delete(customer);
                    System.out.println("Deleted customer: " + customer.getFirstName() + " " + customer.getLastName());
                }
            } else {
                System.out.println("Customer " + customer.getFirstName() + " has no associated account.");
            }
        }
    }

}
