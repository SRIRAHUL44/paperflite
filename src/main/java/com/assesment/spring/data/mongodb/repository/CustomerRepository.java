package com.assesment.spring.data.mongodb.repository;

import com.assesment.spring.data.mongodb.model.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {
    Page<CustomerEntity> findByAccountId_Id(String accountId, Pageable pageable);
}
