package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.StorageCredentials;

@Repository
public interface StorageCredentialRepository extends MongoRepository<StorageCredentials, String>{

}
