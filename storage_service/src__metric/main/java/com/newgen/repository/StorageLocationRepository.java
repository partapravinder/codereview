package com.newgen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.newgen.model.StorageLocation;

@Repository
public interface StorageLocationRepository extends MongoRepository<StorageLocation, String>{
	//List<StorageLocation> findByBlobUriInAndTanantId(List<String> blobUris, String tenantId);
	Optional<StorageLocation> findByBlobUri(String blobUris);
}
