package com.newgen.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.newgen.exception.CustomException;
import com.newgen.model.AsyncFolderOperation;
import com.newgen.model.AsyncFolderOperation.Action;
import com.newgen.model.AsyncFolderOperation.Status;
import com.newgen.model.Folder;
import com.newgen.model.RedisQueue;
import com.newgen.service.AsyncFolderService;
import com.newgen.service.FolderService;
import com.newgen.service.LockService;

@Component
public class CronJobController {

	private static final Logger logger = LoggerFactory.getLogger(CronJobController.class);

	@Autowired
	LockService lockService;

	@Autowired
	RedisQueue redisQueue;

	@Autowired
	AsyncFolderService asyncFolderService;

	@Autowired
	FolderService folderService;

	public void handleAsyncFolderOperations()
			throws CustomException, JsonParseException, JsonMappingException, IOException {
		String token = null;
		String tenantId = "";
		AsyncFolderOperation asyncFolderOperation = null;
		try {
			// Poll the token from the redis queue
			token = redisQueue.pollFirst();

			if (token != null) {
				// Fetch the asyncFolderOperation data from the redis db
				asyncFolderOperation = asyncFolderService.findOne(token, tenantId);
				if (asyncFolderOperation != null) {

					logger.debug("AsyncOperation folder: " + asyncFolderOperation);
					/*
					 * Update the status of the async Folder operation to IN_PROGRESS
					 */
					tenantId = asyncFolderOperation.getTenantId();
					asyncFolderOperation = asyncFolderService.updateStatus(token, Status.IN_PROGRESS, tenantId);

					// Copy Folder Operation
					if (asyncFolderOperation.getAction() == Action.COPY) {
						Folder copiedFolder = folderService.copyFolderAndChildren(
								asyncFolderOperation.getSourceFolderId(), asyncFolderOperation.getTargetFolderId(),
								token, tenantId);

						if (copiedFolder != null) {
							// Update the stats of the parent folder
							// folderService.updateStats(asyncFolderOperation.getTargetFolderId(),
							// tenantId);
						}
					} else if (asyncFolderOperation.getAction() == Action.DELETE) {
						folderService.deleteFolderAndChildren(asyncFolderOperation.getSourceFolderId(), tenantId);
					}
					// Update the status of the copy Folder to Completed
					asyncFolderService.updateStatus(token, Status.COMPLETED, tenantId);
				}
			}
		} catch (CustomException ex) {
			// Change the status to FAILED if there is an exception
			if (token != null && !token.isEmpty() && asyncFolderOperation != null) {
				// Update the status of the async Folder Operation to Completed
				asyncFolderService.updateStatus(token, Status.FAILED, tenantId);
			}
			throw ex;
		}
	}

	public void deleteCompletedFailedAsyncFolderOperations() throws CustomException {
		// Find all async Folder Operation which which have been completed and failed
		// for more than 5 mins
		List<AsyncFolderOperation> asyncFolderOperationList = asyncFolderService.findAllCompletedAndFailed();
		for (AsyncFolderOperation asyncFolderOperation : asyncFolderOperationList) {
			// Delete the async Folder Operation
			String tenantId = asyncFolderOperation.getTenantId();
			asyncFolderService.delete(asyncFolderOperation.getId(), tenantId);
		}
	}

	public void uploadLogFile() throws CustomException, Exception, Exception {

		List<File> filesInFolder = Files.walk(Paths.get("d:/log")).filter(Files::isRegularFile).map(Path::toFile)
				.collect(Collectors.toList());

		CloudBlobContainer blobcontainer = null;
		CloudStorageAccount storageAccount = null;
		CloudBlockBlob blob = null;
		CloudBlobClient blobClient = null;
		String conn = "DefaultEndpointsProtocol=https;AccountName=csg0dd2183fd41dx444bx992;AccountKey=KAiydalpISNtxMXW/7+RhpKnRS89u4JHT6mihm24pUfU1ah+dPIAvGutKrzkDR+XyNC/AzFc3v8CCfMLUx82fQ==;EndpointSuffix=core.windows.net";

		storageAccount = CloudStorageAccount.parse(conn);
		blobClient = storageAccount.createCloudBlobClient();
		blobcontainer = blobClient.getContainerReference("logs");
		blobcontainer.createIfNotExist();

		for (int i = 0; i < filesInFolder.size(); i++) {
			String filename = filesInFolder.get(i).getName();
			String path = filesInFolder.get(i).getPath();

			String deleteDate = deleteRecords();
			blob = blobcontainer.getBlockBlobReference(deleteDate + filename);
			// boolean status = blob.deleteIfExists();

			String localDate = getLocalDate();
			blob = blobcontainer.getBlockBlobReference(localDate + filename);
			File sourceFile = new File(path);
			FileInputStream inputStream = new FileInputStream(sourceFile);
			blob.upload(inputStream, sourceFile.length());
		}
	}

	private String getLocalDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH");
		Date date = new Date();
		String localDate = formatter.format(date);
		localDate = "logs-" + localDate + "/";
		return localDate;
	}

	private String deleteRecords() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH");
		Date date = new Date();
		Date daysAgo = new DateTime(date).minusDays(7).toDate();
		String localDate = formatter.format(daysAgo);
		localDate = "logs-" + localDate + "/";
		return localDate;
	}

}
