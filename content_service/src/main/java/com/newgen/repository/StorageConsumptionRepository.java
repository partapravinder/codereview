package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.StorageConsumption;;

@Repository
public interface StorageConsumptionRepository extends MongoRepository<StorageConsumption, String> {
}

 