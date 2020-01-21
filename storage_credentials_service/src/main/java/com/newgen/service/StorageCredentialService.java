package com.newgen.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.StorageCredentialDao;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageCredentials;

@Service
public class StorageCredentialService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(StorageCredentialService.class);

	@Autowired
	StorageCredentialDao storageCredentialDao;

	public StorageCredentials insert(StorageCredentials storageCredentials,String tenantId) {
		logger.debug("Creating storage credentials: " + storageCredentials.toString());
		return storageCredentialDao.insert(storageCredentials,tenantId);
	}

	public StorageCredentials findById(String id,String tenantId) throws CustomException {
		logger.debug("Finding a storage credential by id");
		return storageCredentialDao.findById(id,tenantId);
	}

	public List<StorageCredentials> list(String tenantId) throws CustomException {
		logger.debug("List all credentials");
		return storageCredentialDao.findAll();
	}

	public void delete(String id, String version,String tenantId) throws CustomException {
		if (version == null || version.isEmpty()) {
			if (storageCredentialDao.findAndRemoveById(id,tenantId) == null) {
				throwStorageCredentialNotFoundException();
			}
		} else {
			if (storageCredentialDao.findAndRemoveByIdAndVersion(id, version,tenantId) == null) {
				StorageCredentials storageCredentials = storageCredentialDao.findById(id,tenantId);
				if (storageCredentials == null) {
					throwStorageCredentialNotFoundException();
				} else {
					if (!Long.toString(storageCredentials.getVersion()).equalsIgnoreCase(version)) {
						throwVersionConflictException();
					} else {
						throwUnknownErrorException();
					}
				}
			}
		}
	}

	public StorageCredentials update(String id, Map<String, String> updateParams, Long version,String tenantId) throws CustomException {
		StorageCredentials storageCredentials = storageCredentialDao.findAndModify(id, updateParams, version,tenantId);

		if (storageCredentials == null) {
			storageCredentials = storageCredentialDao.findById(id,tenantId);
			if (storageCredentials == null) {
				throwStorageCredentialNotFoundException();
			} else {
				if (version != null
						&& !Long.toString(storageCredentials.getVersion()).equalsIgnoreCase(Long.toString(version))) {
					throwVersionConflictException();
				} else {
					throwUnknownErrorException();
				}
			}
		}
		return storageCredentials;
	}

}
