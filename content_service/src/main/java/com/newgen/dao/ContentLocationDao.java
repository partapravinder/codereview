package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.ContentLocation;

public interface ContentLocationDao {
	public ContentLocation findAndRemoveById(String id,String tenantId);

	public ContentLocation findAndRemoveByIdAndVersion(String id, String version,String tenantId);

	public ContentLocation insert(ContentLocation contentLocation,String tenantId);
	
	public ContentLocation findById(String id,String tenantId);
	
	public List<ContentLocation> findByStorageLocationIds(List<String> ids,String tenantId);

	public ContentLocation findAndModify(String id, Map<String, String> updateParams, Long version,String tenantId);

	public ContentLocation findAndIncrementSharedCount(String id,String tenantId);

	public ContentLocation findAndDecrementSharedCount(String id,String tenantId);

	public List<ContentLocation> findAllDeletedContentLocations();

	public List<ContentLocation> findAllDanglingContentLocations();

	public ContentLocation findAndDeleteByLocationId(String locationId,String tenantId);
}
