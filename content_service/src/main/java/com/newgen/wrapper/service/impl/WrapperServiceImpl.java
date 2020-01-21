package com.newgen.wrapper.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.FolderDao;
import com.newgen.dao.LockDao;
import com.newgen.dto.StoreContentDTO;
import com.newgen.enumdef.Flag;
import com.newgen.enumdef.Privilege;
import com.newgen.exception.CustomException;
import com.newgen.logger.RequestCorrelation;
import com.newgen.model.Content;
import com.newgen.model.ContentLocation;
import com.newgen.model.FilterItemCache;
import com.newgen.model.Folder;
import com.newgen.model.Lock;
import com.newgen.service.ContentLocationService;
import com.newgen.service.ContentService;
import com.newgen.service.LockService;
import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "production", "default" })
public class WrapperServiceImpl extends ExceptionThrower implements WrapperService {

	private static final Logger logger = LoggerFactory.getLogger(WrapperServiceImpl.class);

	@Value("${storage.service.url}")
	private String storageUrl;

	@Value("${identity.service.url}")
	private String CheckuploadLimitUrl;

//	@Value("${service.storage.serviceId}")
//	private String storageServiceId;
//
//	@Autowired
//	private EurekaUrlResolver eurekaUrlResolver;

	@Autowired
	private RestTemplate restTemplate;

	private String storeApiPath = "/store";

	@Value("${upload.folder}")
	private String UPLOADED_FOLDER;

	private String storageType = "AZURE_BLOB";

	@Autowired
	ContentLocationService contentLocationService;

	@Autowired
	ContentService contentService;

	@Autowired
	LockService lockService;

	@Autowired
	FolderDao folderDao;

	@Autowired
	LockDao lockDao;

	@Override
	public void acknowledgeStorageService(String token, String tenantId) {
		// String storageUrl = eurekaUrlResolver.procureUrl(storageServiceId);
		logger.debug("Acknowledge upload for token: " + token);
		// Query parameters
		HttpHeaders headers = new HttpHeaders();

		// headers.set("jwt","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");
		headers.set("tenantId", tenantId);

		HttpEntity<String> request = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(storageUrl + "/store/acknowledge");
		// Add query parameter
		if (token != null && !token.isEmpty()) {
			builder.queryParam("token", token);
		}
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, request,
				String.class);
		logger.debug("Acknowledge response:" + response.toString());
	}

	public ResponseEntity<String> uploadStoreContent(MultipartFile file, String name, String contentType,
			String comments, String parentFolderId, String ownerName, String ownerId, String storageCredentialId,
			String uploadPath, Boolean Async, String tenantId)
			throws CustomException, JsonProcessingException, InterruptedException {
		ObjectMapper mapper = new ObjectMapper();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response;

		// Upload the file using storage service
		StoreContentDTO storeContentDTO = new StoreContentDTO();
		storeContentDTO.setStorageCredentialId(storageCredentialId);
		storeContentDTO.setContentPath(uploadPath);
		// storeContentDTO.setContainerName(containerName);
		storeContentDTO.setType(storageType);
		headers.set("tenantId", tenantId);
		// headers.set("jwt",
		// "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");

		headers.set("correlationId", RequestCorrelation.getId());
		// String storageUrl = eurekaUrlResolver.procureUrl(storageServiceId);
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(storeContentDTO), headers);

		if (Async == true)
			response = restTemplate.exchange(storageUrl + storeApiPath + "/upload", HttpMethod.POST, request,
					String.class);
		else
			response = restTemplate.exchange(storageUrl + storeApiPath + "/uploadSync", HttpMethod.POST, request,
					String.class);

		logger.debug("--------------" + response.getBody());
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Content uploadMetaContent(MultipartFile file, String name, String contentType, String comments,
			String parentFolderId, String ownerName, String ownerId, String storageCredentialId, String token,
			String noOfPages, String documentSize, String documentType, String metadata, Privilege privilege,
			Boolean async, String locationId_temp, String tenantId, boolean initialVersion, String dataclass)
			throws CustomException, JsonProcessingException, InterruptedException {

		Map<String, String> metamap = null;
		if (metadata != null) {
			ObjectMapper obj_mapper = new ObjectMapper();
			try {
				metamap = obj_mapper.readValue(metadata, Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, String> dataclassMap = null;
		if (dataclass != null) {
			ObjectMapper obj_mapper = new ObjectMapper();
			try {
				dataclassMap = obj_mapper.readValue(dataclass, Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringBuffer dataClassText = new StringBuffer();
		if(dataclassMap != null) {
			Iterator<Entry<String, String>> itr = dataclassMap.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<String, String> entry = itr.next();
				String key = entry.getKey(); 
				String value = entry.getValue();
				if(key.compareToIgnoreCase("id") != 0 && value != null) {
					dataClassText.append(value.toLowerCase() + " ");
				}
			}
		}

		Content content = null;
		String lockId = null;
		String contentLocationLockId = null;
		String guid = UUID.randomUUID().toString();
		try {

			// Lock on parentFolderId
			lockId = parentFolderId;
			getSharedLock(lockId, guid, tenantId);

			// Check if parentFolderId exists or not
			Folder parentFolder = findById(parentFolderId, tenantId);

			if (parentFolder != null) {

				// Create temp content location
				ContentLocation contentLocation = new ContentLocation(null, null, 1, new Date(), null, null, tenantId);
				if (async == true)
					contentLocation.setFlag(Flag.UNCOMMITTED);
				else {
					contentLocation.setFlag(Flag.COMMITTED);
					if (locationId_temp != null) {
						contentLocation.setLocationId(locationId_temp);
					}
					contentLocation.setAccessDateTime(new Date());
					contentLocation.setRevisedDateTime(new Date());
				}
				contentLocation = contentLocationService.insert(contentLocation, tenantId);
				if (contentLocation != null) {
					// Create temp Content

					Date date = new Date();
					content = new Content(null, name, contentType, comments, parentFolderId, ownerName, ownerId,
							contentLocation.getId(), date, null, null, noOfPages, documentType, documentSize, metamap,
							tenantId, false, null, null, null, null, true, null, null, dataclassMap);
					content.setRevisedDateTime(date);
					content.setToken(token);
					content.setDataClassText(dataClassText.toString());
					if (async == true)
						content.setFlag(Flag.UNCOMMITTED);
					else {
						content.setFlag(Flag.COMMITTED);
						content.setAccessDateTime(new Date());
						content.setRevisedDateTime(new Date());
					}
					if (initialVersion) {
						content.setVersion(BigDecimal.valueOf(1.0));
						content.setLatest(true);
					}

					if (privilege != null) {
						content.setPrivilege(privilege);
					} else {
						content.setPrivilege(Privilege.INHERITED);
					}

					// Insert the content
					logger.debug("Creating content: " + content);
					content = contentService.insert(content, tenantId);
					// content.setPrimaryContentId(content.getId());

					// if (content.getPrimaryContentId()==null ||
					// content.getPrimaryContentId().isEmpty()) {
					if (initialVersion) {
						JSONObject updateContentParams = new JSONObject();
						updateContentParams.put("primaryContentId", content.getId());
						content = contentService.update(content.getId(), updateContentParams.toString(), true,
								tenantId);
					}

				} else {
					throwFailedToCreateContentLocation();
				}

			} else {
				throwParentFolderNotFoundException();
			}
		} finally {
			if (contentLocationLockId != null && !contentLocationLockId.isEmpty()) {
				try {
					releaseLock(contentLocationLockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + contentLocationLockId);// NOSONAR
				}
			}
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit createContent()");
		return content;

	}

	public void releaseLock(String id, String tenantId) throws Exception {
		lockService.delete(id, tenantId);
	}

	public Lock getSharedLock(String id, String guid, String tenantId) throws CustomException {
		return lockService.getLock(id, guid, "shared", tenantId);
	}

	@Override
	public void deleteContent(String id, BigDecimal version, String tenantId) throws CustomException {
		logger.debug("Entering deleteContent()");
		// delete the content
		logger.debug("Deleting Content with id: " + id);
		contentService.markDeleteContent(id, version, tenantId);
		logger.debug("Exit deleteContent()");
	}

	public List<Content> searchContents(HttpServletRequest request, String tenantId) {
		Map<String, String[]> allRequestParams = request.getParameterMap();
		return contentService.search(allRequestParams, tenantId);
	}

	public List<Content> searchContentsByPage(HttpServletRequest request, int pno, String tenantId) {
		Map<String, String[]> allRequestParams = request.getParameterMap();
		return contentService.searchByPage(allRequestParams, tenantId, pno);
	}

	public Content updateContentInfo(String updateContentParams, String id, BigDecimal version, String tenantId)
			throws CustomException {
		logger.debug("Entering updateContent()");
		Content content = null;
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		try {
			// lock on content id
			lockId = id;
			getSharedLock(lockId, guid, tenantId);

			content = contentService.update(id, updateContentParams, version, tenantId);

		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit updateContent()");
		return content;
	}

	public Folder findById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a folder by id");
		return folderDao.findById(id, tenantId);
	}

	public Content moveContent(String id, String targetFolderId, BigDecimal version, String tenantId)
			throws CustomException {
		String lockId = null;
		Content content = null;
		String guid = UUID.randomUUID().toString();
		try {
			// shared lock on target folderId
			lockId = targetFolderId;
			getSharedLock(lockId, guid, tenantId);

			// check if the target folder exists or not
			Folder targetFolder = findById(targetFolderId, tenantId);
			if (targetFolder != null) {
				content = contentService.moveContent(id, targetFolderId, version, tenantId);
			} else {
				logger.debug("Target Folder not found with id: " + targetFolderId);
				throwTargetFolderNotFoundException();
			}
		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit moveContent()");
		return content;
	}

	public ResponseEntity<String> getContentUploadStatus(String token, String tenantId) {
		// String storageUrl = eurekaUrlResolver.procureUrl(storageServiceId);
		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(storageUrl + "/store/status");
		// Add query parameter
		if (token != null) {
			builder.queryParam("token", token);
		}
		HttpHeaders headers = new HttpHeaders();
//		headers.set("jwt",
//				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");

		headers.set("tenantId", tenantId);
		headers.set("correlationId", RequestCorrelation.getId());
		HttpEntity<String> request = new HttpEntity<>("", headers);

		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request,
				String.class);

		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	public ResponseEntity<String> retrieveContent(String id, String tenantId) throws CustomException, JSONException {
		logger.debug("Entering retrieveContent()");
		// String storageUrl = eurekaUrlResolver.procureUrl(storageServiceId);
		HttpHeaders headers = new HttpHeaders();
		headers.set("correlationId", RequestCorrelation.getId());
		headers.set("tenantId", tenantId);
//		headers.set("jwt",
//				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg");

		HttpEntity<String> request = new HttpEntity<>("", headers);

		String storageLocationId = id;

		ResponseEntity<String> response = restTemplate.exchange(storageUrl + "/store/retrieve/" + storageLocationId,
				HttpMethod.GET, request, String.class);

		logger.debug("Exit retrieveContent()");
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}

	@Override
	public Content fetchContentModel(String id, String tenantId) throws CustomException {

		logger.debug("Entering fetchContentModel()");
		Content content = contentService.findById(id, tenantId);
		logger.debug("Exit fetchContentModel()");
		return content;
	}

	@Override
	public Content fetchLatestContentModel(String id, String tenantId) throws CustomException {

		logger.debug("Entering fetchContentModel()");
		Content content = contentService.findLatestById(id, tenantId);
		logger.debug("Exit fetchContentModel()");
		return content;
	}

	@Override
	public JSONObject getFolderHierarchy(String folderId, String tenantId) throws CustomException {
		JSONObject hierarchy_json = new JSONObject();
		JSONArray folder_arr = new JSONArray();
		Folder folder = null;
		do {
			folder = contentService.findFolderById(folderId, tenantId);
			if (folder != null && !folder.getFolderType().toString().equalsIgnoreCase("cabinet")) {
				JSONObject folder_json_temp = new JSONObject(folder);
				folder_arr.put(folder_json_temp);
				folderId = folder.getParentFolderId().toString();
			}
		} while (!folder.getFolderType().toString().equalsIgnoreCase("cabinet"));
		JSONObject cabinet_json = new JSONObject(folder);
		hierarchy_json.put("folder", folder_arr);
		hierarchy_json.put("cabinet", cabinet_json);
		return hierarchy_json;
	}

	public boolean checkStorageLimits(String size, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("authToken", authToken);
		headers.set("size", size);
		HttpEntity<String> request = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(CheckuploadLimitUrl + "/tenants/updatestorage");
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request,
				String.class);
		JSONObject resJson = null;
		resJson = new JSONObject(response.getBody());
		boolean canUpload = resJson.getBoolean("canUpload");
		return canUpload;
	}

	@Override
	public Lock lockContent(Lock lock) {
		lockDao.insert(lock);
		return lockDao.insert(lock);
	}

	@Override
	public Content checkOutContent(String id, String tenantId, String userId) throws CustomException {
		logger.debug("Entering updateContent()");
		Content content = null;
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		try {
			// lock on content id
			lockId = id;
			getSharedLock(lockId, guid, tenantId);

			content = contentService.checkOut(id, tenantId, userId);

		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit updateContent()");
		return content;
	}

	@Override
	public Content undoCheckOutContent(String id, String tenantId, String userId) throws CustomException {
		logger.debug("Entering updateContent()");
		Content content = null;
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		try {
			// lock on content id
			lockId = id;
			getSharedLock(lockId, guid, tenantId);

			// content = contentService.update(id, updateContentParams, version,tenantId);
			content = contentService.undoCheckOut(id, tenantId, userId);

		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit updateContent()");
		return content;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Content checkInMetaContent(MultipartFile file, String name, String contentType, String comments,
			String parentFolderId, String ownerName, String ownerId, String storageCredentialId, String token,
			String noOfPages, String documentSize, String documentType, String metadata, Privilege privilege,
			Boolean async, String locationId_temp, String tenantId, boolean initialVersion, String primaryContentId,
			Date creationDateTime, BigDecimal previousVersion, BigDecimal version, String checkedInBy, String dataclass)
			throws CustomException, JsonProcessingException, InterruptedException {

		Map<String, String> metamap = null;
		if (metadata != null) {
			ObjectMapper obj_mapper = new ObjectMapper();
			try {
				metamap = obj_mapper.readValue(metadata, Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, String> dataclassMap = null;
		if (metadata != null) {
			ObjectMapper obj_mapper = new ObjectMapper();
			try {
				dataclassMap = obj_mapper.readValue(dataclass, Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringBuffer dataClassText = new StringBuffer();
		if(dataclassMap != null) {
			Iterator<Entry<String, String>> itr = dataclassMap.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<String, String> entry = itr.next();
				String key = entry.getKey(); 
				String value = entry.getValue();
				if(key.compareToIgnoreCase("id") != 0 && value != null) {
					dataClassText.append(value.toLowerCase() + " ");
				}
			}
		}

		Content content = null;
		String lockId = null;
		String contentLocationLockId = null;
		String guid = UUID.randomUUID().toString();
		try {

			// Lock on parentFolderId
			lockId = parentFolderId;
			getSharedLock(lockId, guid, tenantId);

			// Check if parentFolderId exists or not
			Folder parentFolder = findById(parentFolderId, tenantId);

			if (parentFolder != null) {

				// Create temp content location
				ContentLocation contentLocation = new ContentLocation(null, null, 1, new Date(), null, null, tenantId);
				if (async == true)
					contentLocation.setFlag(Flag.UNCOMMITTED);
				else {
					contentLocation.setFlag(Flag.COMMITTED);
					if (locationId_temp != null) {
						contentLocation.setLocationId(locationId_temp);
					}
					contentLocation.setAccessDateTime(new Date());
					contentLocation.setRevisedDateTime(new Date());
				}
				contentLocation = contentLocationService.insert(contentLocation, tenantId);
				if (contentLocation != null) {
					// Create temp Content
					content = new Content(null, name, contentType, comments, parentFolderId, ownerName, ownerId,
							contentLocation.getId(), creationDateTime, null, null, noOfPages, documentType,
							documentSize, metamap, tenantId, false, null, null, null, primaryContentId, true,
							previousVersion, version, dataclassMap);

					content.setToken(token);
					content.setLastCheckedInBy(checkedInBy);
					content.setDataClassText(dataClassText.toString());
					if (async == true)
						content.setFlag(Flag.UNCOMMITTED);
					else {
						content.setFlag(Flag.COMMITTED);
						content.setAccessDateTime(new Date());
						content.setRevisedDateTime(new Date());
					}

					if (privilege != null) {
						content.setPrivilege(privilege);
					} else {
						content.setPrivilege(Privilege.INHERITED);
					}

					// Insert the content
					logger.debug("Creating content: " + content);
					content = contentService.insert(content, tenantId);

				} else {
					throwFailedToCreateContentLocation();
				}

			} else {
				throwParentFolderNotFoundException();
			}
		} finally {
			if (contentLocationLockId != null && !contentLocationLockId.isEmpty()) {
				try {
					releaseLock(contentLocationLockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + contentLocationLockId);// NOSONAR
				}
			}
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit createContent()");
		return content;

	}

	@Override
	public Content setContentVersionLatest(String id, BigDecimal version, String tenantId) throws CustomException {
		logger.debug("Entering setContentVersionLatest()");
		Content content = null;
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		try {
			// lock on content id
			lockId = id;
			getSharedLock(lockId, guid, tenantId);

			// content = contentService.update(id, updateContentParams, version,tenantId);
			content = contentService.setContentVersionLatest(id, version, tenantId);

		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit setContentVersionLatest()");
		return content;
	}

	@Override
	public Content undoCheckOutAfterCheckIn(Content latestContent, String tenantId, String userId)
			throws CustomException {
		logger.debug("Entering updateContent()");
		Content content = null;
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		try {
			// lock on content id
			lockId = latestContent.getId();
			getSharedLock(lockId, guid, tenantId);

			// content = contentService.update(id, updateContentParams, version,tenantId);
			content = contentService.undoCheckOutAfterCheckIn(latestContent, tenantId, userId);

		} finally {
			if (lockId != null && !lockId.isEmpty()) {
				try {
					releaseLock(lockId, tenantId);
				} catch (Exception e1) {
					logger.debug(e1.getMessage(), e1);
					logger.debug("Exception in releasing lock for id: " + lockId);// NOSONAR
				}
			}
		}
		logger.debug("Exit updateContent()");
		return content;
	}

}
