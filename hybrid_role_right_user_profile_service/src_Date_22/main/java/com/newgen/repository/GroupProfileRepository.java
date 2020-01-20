package com.newgen.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.GroupProfile;

@Repository
public interface GroupProfileRepository extends MongoRepository<GroupProfile, String> {
}
