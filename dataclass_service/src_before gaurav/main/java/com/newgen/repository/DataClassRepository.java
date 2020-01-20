package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.DataClass;

@Repository
public interface DataClassRepository extends MongoRepository<DataClass, String> {

	DataClass findByDataClassName(String dataClassName, String tenantId);

}
