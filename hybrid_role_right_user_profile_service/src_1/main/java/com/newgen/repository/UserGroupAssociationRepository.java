package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.UserGroupAssociation;

@Repository
public interface UserGroupAssociationRepository extends MongoRepository<UserGroupAssociation, String>{
	//public UserGroupAssociation findByProfileParentFolderId(String parentFolderId);
}
