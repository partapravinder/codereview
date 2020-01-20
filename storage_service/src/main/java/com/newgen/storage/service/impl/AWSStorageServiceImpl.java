package com.newgen.storage.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newgen.controller.ExceptionThrower;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageCredentials;
import com.newgen.model.StorageLocation;
import com.newgen.storage.service.ThirdPartyStorageService;

public class AWSStorageServiceImpl extends ExceptionThrower implements ThirdPartyStorageService {
	private static final Logger logger = LoggerFactory.getLogger(AWSStorageServiceImpl.class);

	public String upload(String contentPath, StorageCredentials storageCredentials, String containerName, String documentId)
			throws CustomException {
		logger.debug("Uploading document to AWS");
		return "";

	}

	public String reliableUpload(String contentPath, StorageCredentials storageCredentials, String containerName)
			throws CustomException {
		logger.debug("In reliable upload" + new Date());
		return null;
	}

	public String delete(StorageLocation storageLocation, StorageCredentials storageCredentials) throws CustomException {
		logger.debug("Deleting " + storageLocation.toString());

		return "";
	}

	public String reliableDelete(String contentPath, StorageCredentials storageCredentials, String containerName)
			throws Exception {
		logger.debug("In reliable delete " + new Date());
		return null;
	}

	public String download(StorageLocation storageLocation, StorageCredentials storageCredentials) throws CustomException {
		logger.debug("Downloading " + storageLocation.toString());
		String documentURI = "";

		return documentURI;
	}
}
