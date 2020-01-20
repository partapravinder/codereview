package com.newgen.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.Content;
import com.newgen.model.ContentLocation;

@Repository
public interface ContentLocationRepository extends MongoRepository<ContentLocation, String> {
	public ContentLocation findByLocationIdAndTenantId(String id, String tenantId);
}
