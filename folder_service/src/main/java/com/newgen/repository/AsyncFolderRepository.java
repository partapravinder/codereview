package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.AsyncFolderOperation;

@Repository
public interface AsyncFolderRepository extends MongoRepository<AsyncFolderOperation, String>{

}
