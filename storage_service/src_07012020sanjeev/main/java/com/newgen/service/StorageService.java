package com.newgen.service;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.ServiceUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.StorageException;
import com.newgen.controller.ExceptionThrower;
import com.newgen.exception.CustomException;
import com.newgen.factory.ThirdPartyStorageFactory;
import com.newgen.model.Content;
import com.newgen.model.ContentLocation;
import com.newgen.model.StorageCredentials;
import com.newgen.model.StorageLocation;
import com.newgen.repository.ContentLocationRepository;
import com.newgen.repository.ContentRepository;

@Service
public class StorageService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

	@Autowired
	ThirdPartyStorageFactory thirdPartyStorageFactory;

	@Autowired
	StorageLocationService storageLocationService;

	@Autowired
	StorageCredentialService storageCredentialService;
	

	@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000), value = ServiceUnavailableException.class)
	public StorageLocation upload(String contentPath, String storageCredentialId, String type, String tenantId)
			throws Exception {
		logger.debug("Uploading File at path: ************** " + contentPath);
		// Store the file in azure blob
		StorageLocation storageLocation = null;
		StorageCredentials storageCredentials = storageCredentialService.findById(storageCredentialId,tenantId);
		if (storageCredentials != null) {
			String blobUri = callThirdPartyUpload(contentPath, storageCredentials, storageCredentials.getContainerName(),type);

			if (blobUri == null) {
				logger.debug("Throwing ServiceUnavailableException ---------------");
				throw new ServiceUnavailableException();
			}

			// Store the location information in storage location collection in
			// mongodb
			storageLocation = new StorageLocation(null, blobUri, storageCredentialId, storageCredentials.getContainerName(), type, new Date(),
					null, null,tenantId);
			storageLocation = storageLocationService.insert(storageLocation, tenantId);
			logger.debug("Storage location created after upload :" + storageLocation.toString());
			return storageLocation;
		} else {
			throwStorageCredentialNotFoundException();
		}
		return storageLocation;
	}

	public String callThirdPartyUpload(String contentPath, StorageCredentials storageCredentials, String containerName,String type)
			throws Exception {
		logger.debug("Calling third party upload--------------------------------" + new Date());
		return thirdPartyStorageFactory.getStorageService(type).upload(contentPath, storageCredentials, containerName);
	}

	@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000), value = ServiceUnavailableException.class)
	public void delete(String id, String version,String tenantId) throws CustomException, ServiceUnavailableException, InvalidKeyException, URISyntaxException, StorageException {
		// Get the storage location
		StorageLocation storageLocation = storageLocationService.findById(id,tenantId);
		logger.debug("Storage Location for deleting content: "+ storageLocation);
		if (storageLocation != null) {
			logger.debug("Storage Location Found:" + storageLocation.toString());
			StorageCredentials storageCredentials = storageCredentialService
					.findById(storageLocation.getStorageCredentialId(),tenantId);
			if (storageCredentials != null) {
				logger.debug("Storage Credentials Found:" + storageCredentials.toString());
				// Delete the content from storage
				String result = thirdPartyStorageFactory.getStorageService(storageLocation.getType()).delete(storageLocation,
						storageCredentials);
				logger.debug("Result: "+ result);
				if (result == null) {
					logger.debug("Throwing ServiceUnavailableException ---------------");
					throw new ServiceUnavailableException();
				}
				logger.debug("Deleting storage location: " + id);
				// Delete the location information from storage location
				storageLocationService.delete(id, version,tenantId);
			} else {
				logger.debug("Storage credential not found");
				throwStorageCredentialNotFoundException();
			}
		} else {
			logger.debug("Storage location not found");
			throwStorageLocationNotFoundException();
		}
	}

	public String retreiveContent(String id,String tenantId) throws CustomException, InvalidKeyException, StorageException, URISyntaxException {
		String downloadUrl = null;
		// Get the storage location
		StorageLocation storageLocation = storageLocationService.findById(id,tenantId);
		if (storageLocation != null) {	
			StorageCredentials storageCredentials = storageCredentialService
					.findById(storageLocation.getStorageCredentialId(),tenantId);
			if (storageCredentials != null) {
				// Get the download url for the blob
				downloadUrl = thirdPartyStorageFactory.getStorageService(storageLocation.getType()).download(storageLocation,
						storageCredentials);
			} else {
				throwStorageCredentialNotFoundException();
			}
		} else {
			throwStorageLocationNotFoundException();
		}
		return downloadUrl;
	}
	
	public List<StorageLocation> retreiveStorageIds(List<String> blobUri, String tenantId) throws CustomException, InvalidKeyException, StorageException, URISyntaxException {
		// Get the storage location
		return storageLocationService.findByStorageUri(blobUri, tenantId);
	}

}
