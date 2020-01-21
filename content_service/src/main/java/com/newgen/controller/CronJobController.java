package com.newgen.controller;

import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.ContentLocation;
import com.newgen.service.ContentLocationService;
import com.newgen.service.ContentService;
import com.newgen.service.LockService;

@Component
public class CronJobController {

	private static final Logger logger = LoggerFactory.getLogger(CronJobController.class);
	@Autowired
	ContentService contentService;

	@Autowired
	ContentLocationService contentLocationService;

	@Autowired
	LockService lockService;

	public void handleDeleteContent() throws CustomException {
		String guid = UUID.randomUUID().toString();
		String lockId = null;
		List<Content> contentList = contentService.findAllDeletedContents();
		for (Content content : contentList) {
			try {

				if (content.getContentLocationId() != null && !content.getContentLocationId().isEmpty()) {
					// First remove the contentLocation link from content

					String tenantId = content.getTenantId();
					lockId = content.getContentLocationId();
					lockService.getLock(lockId, guid, "exclusive", tenantId);
					contentService.deleteContentLocationLink(content.getId(), tenantId);

					// decrease the count on content location
					contentLocationService.decreaseCount(content.getContentLocationId(), tenantId);
					lockService.releaseLock(lockId, tenantId);
					lockId = null;

				}
				// delete the content
				String tenantId = content.getTenantId();
				contentService.deleteContent(content.getId(), null, tenantId);
				// TODO set the random sleep time, to break the high frequency
				// delete pattern
				// Thread.sleep(arg0);
			} finally {
				if (lockId != null && !lockId.isEmpty()) {
					try {

						String tenantId = content.getTenantId();
						lockService.releaseLock(lockId, tenantId);
					} catch (Exception e1) {
						logger.error(" " + e1.getMessage() + e1);
						logger.error("Exception in releasing lock for id: " + lockId);
					}
				}
			}
		}
	}

	public void handleDeleteContentLocation() throws CustomException, JSONException {
		List<ContentLocation> contentLocationList = contentLocationService.findAllDeletedContentLocations();
		logger.debug("Cron handle marked delete contentlocation count : " + contentLocationList.size());
		for (ContentLocation contentLocation : contentLocationList) {
			// delete the contentLocation
			String tenantId = contentLocation.getTenantId();
			contentLocationService.delete(contentLocation.getId(), null, contentLocation.getLocationId(), tenantId);
			// TODO set the random sleep time, to break the high frequency
			// delete pattern
			// Thread.sleep(arg0);
		}
	}

	public void handleDanglingContentLocation() throws CustomException, JSONException {
		// Find all dangling contents with accessdatetime greater than 2 days
		List<ContentLocation> contentLocationList = contentLocationService.findAllDanglingContentLocations();
		logger.debug("Cron handle dangling contentlocation count : " + contentLocationList.size());
		Content content;
		for (ContentLocation contentLocation : contentLocationList) {
			// Look for a content containing the content location
			String tenantId = contentLocation.getTenantId();
			content = contentService.findContentWithContentLocation(contentLocation.getId(), tenantId);
			if (content == null) {
				// delete the contentLocation
				contentLocationService.delete(contentLocation.getId(), null, contentLocation.getLocationId(), tenantId);
			}
		}

	}

}
