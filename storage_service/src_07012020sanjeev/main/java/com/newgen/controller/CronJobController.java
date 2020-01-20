package com.newgen.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.newgen.exception.CustomException;
import com.newgen.model.RedisQueue;
import com.newgen.model.StorageLocation;
import com.newgen.model.StorageProcess;
import com.newgen.model.StorageProcess.Action;
import com.newgen.model.StorageProcess.Status;
import com.newgen.service.CustomMessageSenderService;
import com.newgen.service.StorageProcessService;
import com.newgen.service.StorageService;

@Component
public class CronJobController {

	private static final Logger logger = LoggerFactory.getLogger(CronJobController.class);

	@Autowired
	RedisQueue redisQueue;

	@Autowired
	StorageProcessService storageProcessService;

	@Autowired
	StorageService storageService;
	
	@Autowired
	CustomMessageSenderService customMessageSenderService;

	public void handleStorageProcess() throws Exception {//NOSONAR
		String id = null;
		StorageProcess storageProcess = null;
		try {
			id = redisQueue.pollFirst();
			while (id != null) {
				logger.debug("Token id to process: " + id);
				storageProcess = storageProcessService.findOne(id);
				logger.debug("Storage process found: " + storageProcess);
				
				String tenantId =  storageProcess.getTenantId();
				// Change the status to IN_PROGRESS
				storageProcess = storageProcessService.updateStatus(id, Status.IN_PROGRESS, null,tenantId);
				if (storageProcess.getAction() == Action.UPLOAD) { // Uploading											// content
					
					logger.debug("[Token "+storageProcess.getId()+"] Uploading Storage Process: " + storageProcess.toString());
					StorageLocation storageLocation = storageService.upload(
							storageProcess.getStoreAction().getContentPath(),
							storageProcess.getStoreAction().getStorageCredentialId(),
							storageProcess.getStoreAction().getType(),tenantId);
					// Change the status to COMPLETED and store the storageLocation Id
					storageProcessService.updateStatus(id, Status.COMPLETED, storageLocation.getId(),tenantId);
					
					//Send message of completion to metadata service to commit the content and content location
					customMessageSenderService.sendMessage(Status.COMPLETED.toString(), id, storageLocation.getId() , tenantId);

				} else if (storageProcess.getAction() == Action.DELETE) { // Deleting
																			// content
					logger.debug("[TokenDelete "+storageProcess.getId()+"] Deleting Storage Process: " + storageProcess.toString());
					storageService.delete(storageProcess.getStoreAction().getId(),
							storageProcess.getStoreAction().getVersion(),tenantId);
					// Change the status to COMPLETED
					storageProcessService.updateStatus(id, Status.COMPLETED, null,tenantId);
				}

				logger.debug("Storage Process: " + storageProcess.toString());

				id = redisQueue.pollFirst();
			}
		} catch (CustomException ex) {
			logger.debug(ex.getMessage());
			// Change the status to FAILED if there is an exception
			if (id != null && !id.isEmpty()) {
				storageProcess = storageProcessService.findOne(id);
				String tenantId = storageProcess.getTenantId();
				storageProcessService.updateStatus(id, Status.FAILED, null,tenantId);
				if(storageProcess != null && storageProcess.getAction() == Action.UPLOAD){
					logger.debug("[Token "+storageProcess.getId()+"] Upload failed");
					//Send message of failure to metadata service to delete the content and content location
					customMessageSenderService.sendMessage(Status.FAILED.toString(), id, "", tenantId);
				}else{
					logger.debug("[TokenDelete "+storageProcess.getId()+"] Delete failed");
				}
			}
			throw ex;
		}
	}

	public void deleteAcknowledFailedStorageProcess() throws CustomException {
		// Find all storage processes which which have been acknowledged and failed for more
		// than 5 mins
		List<StorageProcess> storageProcessList = storageProcessService.findAllAcknowldegedAndFailed();
		for (StorageProcess storageProcess : storageProcessList) {
			// Delete the storage process
			String tenantId = storageProcess.getTenantId();
			storageProcessService.delete(storageProcess.getId(),tenantId);
		}
		
		//Find all completed 2 days ago and mark them acknowledged
		storageProcessList = storageProcessService.findAllCompletedForDays(2);
		for (StorageProcess storageProcess : storageProcessList) {
			// Change the status to ACKNOWLEDGED
			String tenantId = storageProcess.getTenantId();
			storageProcessService.updateStatus(storageProcess.getId(), Status.ACKNOWLEDGED, null,tenantId);
		}
	}
}
