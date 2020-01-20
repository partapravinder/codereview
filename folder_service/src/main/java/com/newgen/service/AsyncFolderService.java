package com.newgen.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.AsyncFolderDao;
import com.newgen.exception.CustomException;
import com.newgen.model.AsyncFolderOperation;
import com.newgen.model.InOutParameters;
import com.newgen.model.AsyncFolderOperation.Status;

@Service
public class AsyncFolderService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(AsyncFolderService.class);

	@Autowired
	AsyncFolderDao asyncFolderDao;

	public AsyncFolderOperation insert(AsyncFolderOperation asyncFolderOperation) {
		logger.debug("Creating copy folder: " + asyncFolderOperation.toString());
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = asyncFolderDao.insert(asyncFolderOperation);
		long endTime = System.nanoTime();
		FolderService fs = new FolderService();
		fs.callLoggingService(asyncFolderOperation.getTenantId(), null, "CosmosDB", startTime, endTime,
				inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "POST",
				"FolderService");
		return inOutParameters.getAsyncFolderOperation();
	}

	public AsyncFolderOperation findOne(String id, String tenantId) {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = asyncFolderDao.findById(id, tenantId);
		long endTime = System.nanoTime();
		FolderService fs = new FolderService();
		fs.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getAsyncFolderOperation();
	}

	public AsyncFolderOperation updateStatus(String id, Status status, String tenantId) {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = asyncFolderDao.updateStatus(id, status, tenantId);
		long endTime = System.nanoTime();
		FolderService fs = new FolderService();
		fs.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "PUT", "FolderService");
		return inOutParameters.getAsyncFolderOperation();
	}

	public List<AsyncFolderOperation> findAllCompletedAndFailed() {
		return asyncFolderDao.findAllCompletedAndFailed();
	}

	public void delete(String id, String tenantId) throws CustomException {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = asyncFolderDao.findAndRemoveById(id, tenantId);
		long endTime = System.nanoTime();
		if (inOutParameters.getAsyncFolderOperation() == null) {
			throwCopyFolderNotFoundException();
		}
		FolderService fs = new FolderService();
		fs.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "DELETE", "FolderService");
	}

	public void updateProgress(String token, String tenantId) {
		asyncFolderDao.updateProgress(token, tenantId);
	}

}
