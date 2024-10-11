package com.assesment.spring.data.mongodb.repository;

import com.assesment.spring.data.mongodb.model.AccountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {
}
