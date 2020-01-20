package com.newgen.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.StorageLocationDao;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageLocation;

@Service
public class StorageLocationService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(StorageLocationService.class);

	@Autowired
	StorageLocationDao storageLocationDao;
	
	public StorageLocation insert(StorageLocation storageLocation,String tenantId) {
		logger.debug("Creating Storage location: " + storageLocation.toString());
		return storageLocationDao.insert(storageLocation,tenantId);
	}

	public StorageLocation findById(String id,String tenantId) {
		logger.debug("Finding Storage location by id: " + id);
		return storageLocationDao.findById(id,tenantId);
	}
	
	public List<StorageLocation> findByStorageUri(List<String> uris,String tenantId) throws CustomException {
		logger.debug("Finding storage location by uri: " + uris);
		return storageLocationDao.findByBlobUris(uris, tenantId);
	}

	public void delete(String id, String version,String tenantId) throws CustomException {
		logger.debug("Deleting Storage location by id: " + id +" and version: " + version);
		if (version == null || version.isEmpty()) {
			if (storageLocationDao.findAndRemoveById(id,tenantId) == null) {
				throwStorageLocationNotFoundException();
			}
		} else {
			if (storageLocationDao.findAndRemoveByIdAndVersion(id, version,tenantId) == null) {
				StorageLocation storageLocation = storageLocationDao.findById(id,tenantId);
				if (storageLocation == null) {
					throwStorageLocationNotFoundException();
				} else {
					if (!Long.toString(storageLocation.getVersion()).equalsIgnoreCase(version)) {
						throwVersionConflictException();
					} else {
						throwUnknownErrorException();
					}
				}
			}
		}
	}
}
