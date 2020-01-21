package com.newgen.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.Content;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {
	public Optional<Content> findById(String id);
	public Optional<Content> findByPrimaryContentIdAndTenantId(String id, String tenantId);
}

 