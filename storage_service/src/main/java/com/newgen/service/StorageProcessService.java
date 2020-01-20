package com.newgen.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.StorageProcessDao;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageProcess;
import com.newgen.model.StorageProcess.Status;

@Service
public class StorageProcessService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(StorageProcessService.class);

	@Autowired
	StorageProcessDao storageProcessDao;

	public StorageProcess insert(StorageProcess storageProcess,String tenantId) {
		logger.debug("Creating storage process: " + storageProcess.toString());
		return storageProcessDao.insert(storageProcess,tenantId);
	}

	public StorageProcess findOne(String id) {
		return storageProcessDao.findById(id);
	}
	public StorageProcess findOnestatus(String id,String tenantId) {
		return storageProcessDao.findById(id);
	}
	

	public StorageProcess updateStatus(String id, Status status, String storageLocationId,String tenantId) {
		return storageProcessDao.updateStatus(id, status, storageLocationId,tenantId);
	}

	public List<StorageProcess> findAllAcknowldegedAndFailed() {
		return storageProcessDao.findAllAcknowldegedAndFailed();
	}

	public void delete(String id,String tenantId) throws CustomException {
		if (storageProcessDao.findAndRemoveById(id,tenantId) == null) {
			throwStorageProcessNotFoundException();
		}
	}

	public List<StorageProcess> findAllCompletedForDays(int days) {
		return storageProcessDao.findAllCompletedForDays(days);
	}
}
