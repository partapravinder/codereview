package com.newgen.dao;

import java.util.List;

import com.newgen.model.StorageLocation;

public interface StorageLocationDao {

	StorageLocation insert(StorageLocation storageLocation,String tenantId);

	StorageLocation findById(String id,String tenantId);
	
	public List<StorageLocation> findByBlobUris(List<String> ids,String tenantId);

	StorageLocation findAndRemoveById(String id,String tenantId);

	StorageLocation findAndRemoveByIdAndVersion(String id, String version,String tenantId);

}
