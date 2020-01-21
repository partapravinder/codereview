package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.Folder;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
	
}
