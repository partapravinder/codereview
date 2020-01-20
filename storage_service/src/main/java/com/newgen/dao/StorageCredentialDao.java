package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.StorageCredentials;

public interface StorageCredentialDao {

	StorageCredentials insert(StorageCredentials storageCredentials,String tenantId);

	StorageCredentials findById(String id,String tenantId);

	StorageCredentials findAndRemoveById(String id,String tenantId);

	StorageCredentials findAndRemoveByIdAndVersion(String id, String version,String tenantId);

	StorageCredentials findAndModify(String id, Map<String, String> updateParams, Long version,String tenantId);

	List<StorageCredentials> findAll();

}
