package com.newgen.storage.service;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.StorageException;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageCredentials;
import com.newgen.model.StorageLocation;

public interface ThirdPartyStorageService {
	
	
	public String upload(String contentPath, StorageCredentials storageCredentials, String containerName) throws CustomException, Exception;//NOSONAR

	public String delete(StorageLocation storageLocation,StorageCredentials storageCredentials) throws CustomException, URISyntaxException, StorageException, InvalidKeyException;

	public String download(StorageLocation storageLocation,StorageCredentials storageCredentials) throws CustomException,StorageException, URISyntaxException, StorageException, InvalidKeyException;

}
