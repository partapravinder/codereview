package com.newgen.dao;

import java.util.List;

import com.newgen.model.StorageProcess;
import com.newgen.model.StorageProcess.Status;

public interface StorageProcessDao {

	StorageProcess insert(StorageProcess storageProcess,String tenantId);

	StorageProcess findById(String id);
	
	StorageProcess findStatusById(String id,String tenantId);


	StorageProcess updateStatus(String id,Status status, String storageLocationId,String tenantId);

	StorageProcess findAndRemoveById(String id,String tenantId);

	List<StorageProcess> findAllAcknowldegedAndFailed();

	List<StorageProcess> findAllCompletedForDays(int days);

}
