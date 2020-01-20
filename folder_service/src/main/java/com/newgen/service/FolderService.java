package com.newgen.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.FolderDao;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.Folder;
import com.newgen.model.InOutParameters;
import com.newgen.model.LogEntity;

@Service
public class FolderService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(FolderService.class);

	public static final String DELETED_PARAM = "deleted";

	@Autowired
	FolderDao folderDao;

	@Value("${identity.service.url}")
	private String identityUrl;

//	@Autowired
//	ContentDao contentDao;

	@Autowired
	private RestTemplate restTemplate;

//	@Value("${service.content.serviceId}")
//	private String contentServiceId;
//
//	@Autowired
//  private EurekaUrlResolver eurekaUrlResolver;

	@Value("${content.service.url}")
	private String contentUrl;

	@Value("${logging.service.url}")
	private String loggingServiceUrl;

	// @Autowired
	// private WrapperService wrapperService;

	@Autowired
	AsyncFolderService copyFolderService;

	public Folder insert(Folder folder) throws CustomException {
		logger.debug("Creating Folder");
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.insert(folder);
		long endTime = System.nanoTime();
		callLoggingService(folder.getTenantId(), null, "CosmosDB", startTime, endTime,
				inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "POST",
				"FolderService");
		return inOutParameters.getFolder();
	}

	public Folder findById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a folder by id");
		long startTime = System.nanoTime();
		InOutParameters f = folderDao.findById(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, f.getRequestPayloadSize(),
				f.getResponsePayloadSize(), "GET", "FolderService");

		return f.getFolder();
	}

	public Folder findCabinetById(String id, String tenantId) throws CustomException {
		long startTime = System.nanoTime();
		logger.debug("Finding a Cabinet by id");
		InOutParameters inOutParameters = folderDao.findCabinetById(id, tenantId);
		long endTime = System.nanoTime();

		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");

		Folder folder = null;
		if (inOutParameters.getFolder() != null) {
			folder = inOutParameters.getFolder();
		}
		return folder;

	}

	public List<Folder> search(Map<String, String[]> allRequestParams, String tenantId) throws CustomException {
		logger.debug("Searching for folders based on : " + allRequestParams);
		long startTime = System.nanoTime();
		InOutParameters inOutParams = folderDao.findAllFolders(allRequestParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParams.getFolders();
	}

	public List<Folder> searchByPage(Map<String, String[]> allRequestParams, String tenantId, int pno)
			throws CustomException {
		logger.debug("Searching for folders based on : " + allRequestParams);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.findAllFoldersByPage(allRequestParams, tenantId, pno);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolders();
	}

	public void delete(String id, String version, String tenantId) throws CustomException {
		if (version == null || version.isEmpty()) {
			long startTime = System.nanoTime();
			InOutParameters inOutParameters = folderDao.findAndRemoveById(id, tenantId);
			long endTime = System.nanoTime();
			callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
					inOutParameters.getResponsePayloadSize(), "DELETE", "FolderService");
			Folder f = inOutParameters.getFolder();
			if (f == null) {
				throwFolderNotFoundException();
			}
		} else {
			long startTime = System.nanoTime();
			InOutParameters findAndRemoveByIdAndVersion = folderDao.findAndRemoveByIdAndVersion(id, version, tenantId);
			long endTime = System.nanoTime();
			callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
					findAndRemoveByIdAndVersion.getRequestPayloadSize(),
					findAndRemoveByIdAndVersion.getResponsePayloadSize(), "DELETE", "FolderService");
			if (findAndRemoveByIdAndVersion.getFolder() == null) {
				long sT = System.nanoTime();
				InOutParameters inOutParameters = folderDao.findById(id, tenantId);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT,
						findAndRemoveByIdAndVersion.getRequestPayloadSize(),
						findAndRemoveByIdAndVersion.getResponsePayloadSize(), "GET", "FolderService");
				Folder folder = inOutParameters.getFolder();
				if (folder == null) {
					throwFolderNotFoundException();
				} else {
					if (!Long.toString(folder.getVersion()).equalsIgnoreCase(version)) {
						throwVersionConflictException();
					} else {
						throwUnknownErrorException();
					}
				}
			}
		}
	}

	public void deleteFolderAndChildren(String id, String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException {

		markDeleteFolder(id, null, tenantId);
		// Iterate through folders and delete the same
		List<Folder> folderList = listFoldersUnderParentFolderId(id, tenantId);
		for (Folder folderObj : folderList) {
			deleteFolderAndChildren(folderObj.getId(), tenantId);
		}

		// Iterate through contents and delete the same
		List<Content> contentList = listContentsUnderParentFolderId(id, tenantId);
		for (Content contentObj : contentList) {
			markDeleteContent(contentObj.getId(), null, tenantId);
		}

		// Delete the folder itself
		delete(id, null, tenantId);
	}

	private void markDeleteContent(String id, String version, String tenantId) {
		// String contentUrl = eurekaUrlResolver.procureUrl(contentServiceId);
		HttpHeaders headers = new HttpHeaders();
		headers.set("tenantId", tenantId);
		HttpEntity<String> request = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(contentUrl + "/contents" + "/" + id);
		// Add query parameter
		if (version != null && !version.isEmpty()) {
			builder.queryParam("version", version);
		}
		restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, request, String.class);
	}

	public void deleteChildFolders(String id, String version, String tenantId) throws CustomException {
		if (version == null || version.isEmpty()) {
			if (folderDao.findAndRemoveById(id, tenantId) == null) {
				throwFolderNotFoundException();
			}
		} else {
			if (folderDao.findAndRemoveByIdAndVersion(id, version, tenantId) == null) {
				InOutParameters inOutParameters = folderDao.findById(id, tenantId);
				Folder folder = inOutParameters.getFolder();
				if (folder == null) {
					throwFolderNotFoundException();
				} else {
					if (!Long.toString(folder.getVersion()).equalsIgnoreCase(version)) {
						throwVersionConflictException();
					} else {
						throwUnknownErrorException();
					}
				}
			}
		}
	}

	public boolean isFolderEmpty(String id, String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException {
		// Check if the id passed exists as a parentFolderId for any content
		// folder
		logger.debug("Entering isFolderEmpty()");
		logger.debug("Finding folders with parentFolderId: " + id);

		List<Folder> folderList = listFoldersUnderParentFolderId(id, tenantId);

		logger.debug("Finding content with parentFolderId: " + id);
		List<Content> contentList = listContentsUnderParentFolderId(id, tenantId);
		if ((folderList != null && !folderList.isEmpty()) || (contentList != null && !contentList.isEmpty())) {
			logger.debug("Folders/Content found!");
			return false;
		} else {
			logger.debug("Folders/Content not found!");
			return true;
		}
	}

	public List<Folder> listFoldersUnderParentFolderId(String id, String tenantId) {

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.findByParentFolderId(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolders();
	}

	public List<Content> listContentsUnderParentFolderId(String id, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		// return contentDao.findByParentFolderId(id, tenantId);
		ObjectMapper mapper = new ObjectMapper();
		// String contentUrl = eurekaUrlResolver.procureUrl(contentServiceId);
		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(contentUrl + "/contents");
		HttpHeaders headers = new HttpHeaders();
		headers.set("tenantId", tenantId);

		HttpEntity<String> requests = new HttpEntity<>(headers);

		// Add query parameter
		builder.queryParam("parentFolderId", id);
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requests,
				String.class);
		List<Content> contentList = mapper.readValue(response.getBody().toString(), new TypeReference<List<Content>>() {
		});
		return contentList;
	}

	public List<Folder> listFoldersForParentFolderId(String parentFolderId, String tenantId) {
		logger.debug("Entering listFoldersForParentFolderId()");
		logger.debug("Finding folders with parentFolderId: " + parentFolderId);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.findByParentFolderId(parentFolderId, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolders();
	}

	public Folder update(String id, String updateFolderParams, Long version, String tenantId) throws CustomException {
		long startTime = System.nanoTime();
		InOutParameters findAndModify = folderDao.findAndModify(id, updateFolderParams, version, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, findAndModify.getRequestPayloadSize(),
				findAndModify.getResponsePayloadSize(), "PUT", "FolderService");
		Folder folder = findAndModify.getFolder();
		if (folder == null) {
			long sT = System.nanoTime();
			InOutParameters inOutParameters = folderDao.findById(id, tenantId);
			long eT = System.nanoTime();
			callLoggingService(tenantId, null, "CosmosDB", sT, eT, findAndModify.getRequestPayloadSize(),
					findAndModify.getResponsePayloadSize(), "GET", "FolderService");
			folder = inOutParameters.getFolder();
			if (folder == null) {
				throwFolderNotFoundException();
			} else {
				if (version != null && !Long.toString(folder.getVersion()).equalsIgnoreCase(Long.toString(version))) {
					throwVersionConflictException();
				} else {
					throwUnknownErrorException();
				}
			}
		}
		return folder;
	}

	public List<Folder> findByName(String name, String tenantId) {
		return folderDao.findByFolderName(name, tenantId);
	}

	public String listChildElements(String parentFolderId, String tenantId)
			throws CustomException, JSONException, JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering listChildElements()");
		List<Folder> folderList;
		List<Content> contentList;
		JSONObject jsonObject = new JSONObject();
		logger.debug("Listing child elements for parentFolderId: " + parentFolderId);
		// folderList = listService.fetchFolderList(parentFolderId,tenantId);
		folderList = listFoldersForParentFolderId(parentFolderId, tenantId);

		// contentList = contentDao.findByParentFolderId(parentFolderId, tenantId);
		contentList = listContentsUnderParentFolderId(parentFolderId, tenantId);
//		contentList = listService.fetchContentList(parentFolderId,tenantId);
		JSONArray jsonFolderArray = new JSONArray(folderList);
		JSONArray jsonContentArray = new JSONArray(contentList);
		jsonObject.put("folders", jsonFolderArray);
		jsonObject.put("contents", jsonContentArray);

		logger.debug("Exit listChildElements()");

		if (folderList.isEmpty() && contentList.isEmpty())
			return "";

		return jsonObject.toString();
	}

	public Folder moveFolder(String id, String targetId, Long version, String tenantId) throws CustomException {
		long startTime = System.nanoTime();
		InOutParameters updateParentFolderId = folderDao.updateParentFolderId(id, targetId, version, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, updateParentFolderId.getRequestPayloadSize(),
				updateParentFolderId.getResponsePayloadSize(), "POST", "FolderService");
		if (updateParentFolderId.getFolder() == null) {
			long sT = System.nanoTime();
			updateParentFolderId = folderDao.findById(id, tenantId);
			long eT = System.nanoTime();
			callLoggingService(tenantId, null, "CosmosDB", sT, eT, updateParentFolderId.getRequestPayloadSize(),
					updateParentFolderId.getResponsePayloadSize(), "GET", "FolderService");
			if (updateParentFolderId.getFolder() == null) {
				throwFolderNotFoundException();
			} else {
				if (version != null && !Long.toString(updateParentFolderId.getFolder().getVersion())
						.equalsIgnoreCase(Long.toString(version))) {
					throwVersionConflictException();
				} else {
					throwUnknownErrorException();
				}
			}
		}
		return updateParentFolderId.getFolder();
	}

	public Folder updateStats(String id, String tenantId) throws JsonParseException, JsonMappingException, IOException {
		// Fetch the child folders and contents

		List<Folder> childFolderList = listFoldersForParentFolderId(id, tenantId);

		// List<Content> childContentList = contentDao.findByParentFolderId(id,
		// tenantId);
		List<Content> childContentList = listContentsUnderParentFolderId(id, tenantId);

//		List<Folder> childFolderList = listService.fetchFolderList(id,tenantId);
//		List<Content> childContentList = listService.fetchContentList(id,tenantId);
		return folderDao.updateChildrenCount(id, childFolderList.size() + childContentList.size(), tenantId);
	}

	public Folder copyFolderAndChildren(String id, String targetFolderId, String token, String tenantId)
			throws CustomException, JsonParseException, JsonMappingException, IOException {
		// Get the source folder Info to copy
		Folder folder = findById(id, tenantId);

		/*
		 * Use the source folder info and create a new folder at the targetfolderId
		 */
		Date date = new Date();
		InOutParameters inOutParameters = new InOutParameters();
		Folder newFolder = new Folder(null, folder.getFolderName(), folder.getFolderType(), folder.getComments(),
				targetFolderId, folder.getOwnerName(), folder.getOwnerId(), date, folder.getUsedFor(),
				folder.getMetadata(), folder.getTenantId());
		newFolder.setRevisedDateTime(date);

		long startTime = System.nanoTime();
		InOutParameters inOutParams = folderDao.insert(newFolder);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "POST", "FolderService");

		newFolder = inOutParameters.getFolder();
		if (newFolder != null) {
			// update the progress on token
			updateProgressOnToken(token, tenantId);
			/*
			 * Iterate through the children of the folder and replicate the same // under
			 * the copied folder Id
			 */

			// Iterate through folders and copy the same under copied folder
			// id
			List<Folder> folderList = listFoldersUnderParentFolderId(id, tenantId);
			for (Folder folderObj : folderList) {
				copyFolderAndChildren(folderObj.getId(), newFolder.getId(), token, tenantId);
			}

			// Iterate through contents and copy the same under copied
			// folder id
			List<Content> contentList = listContentsUnderParentFolderId(id, tenantId);
			for (Content contentObj : contentList) {
				copyContent(newFolder.getId(), contentObj.getId(), tenantId);
				// update the progress on token
				updateProgressOnToken(token, tenantId);
			}
		}
		return newFolder;
	}

	private Content copyContent(String parentFolderId, String id, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		// Content content = null;
		ObjectMapper mapper = new ObjectMapper();
		// String contentUrl = eurekaUrlResolver.procureUrl(contentServiceId);
		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(contentUrl + "/contents" + "/" + id);
		HttpHeaders headers = new HttpHeaders();
		headers.set("tenantId", tenantId);

		HttpEntity<String> request = new HttpEntity<>(headers);

		if (parentFolderId != null) {
			builder.queryParam("parentFolderId", parentFolderId);
		}
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request,
				String.class);
		Content content = mapper.readValue(response.getBody().toString(), Content.class);
		return content;
	}

	private void updateProgressOnToken(String token, String tenantId) {
		copyFolderService.updateProgress(token, tenantId);
	}

	public boolean checkIfFolderIsNotAChild(String id, String targetFolderId, String tenantId) throws CustomException {

		logger.debug("Checking if id: " + id + " is not a parent of folderid: " + targetFolderId);
		Folder folder = findById(targetFolderId, tenantId);

		if (folder == null) {
			throwTargetFolderNotFoundException();
		} else {
			if ("folder".equalsIgnoreCase(folder.getFolderType())) {
				if (folder.getParentFolderId().equalsIgnoreCase(id)) {
					return false;
				} else {
					return checkIfFolderIsNotAChild(id, folder.getParentFolderId(), tenantId);
				}
			} else if ("cabinet".equalsIgnoreCase(folder.getFolderType())) {
				return true;
			}
		}
		return false;
	}

	public void markDeleteFolder(String id, String version, String tenantId) throws CustomException {
		logger.debug("Mark the folder for delete with id: " + id + " and version: " + version);
		JSONObject deleteFlagParam = new JSONObject();
		deleteFlagParam.put("deleted", "true");
		Long versionValue = version == null ? null : Long.valueOf(version);
		update(id, deleteFlagParam.toString(), versionValue, tenantId);
	}

//	public void deleteFolderAndChildren(String id, String tenantId) throws CustomException {
//
//		markDeleteFolder(id, null, tenantId);
//		// Iterate through folders and delete the same
//		List<Folder> folderList = listFoldersUnderParentFolderId(id, tenantId);
//		for (Folder folderObj : folderList) {
//
//			ResponseEntity<String> response = wrapperService.deleteContentByParentFolder(folderObj.getId(), tenantId);
//
//		}
//
//		for (Folder folderObj : folderList) {
//
//			deleteFolderAndChildren(folderObj.getId(), tenantId);
//		}
//
//		// Iterate through contents and delete the same
//		List<Content> contentList = listContentsUnderParentFolderId(id, tenantId);
//		for (Content contentObj : contentList) {
//		}
//
//		// Delete the folder itself
//		delete(id, null, tenantId);
//	}

	public List<Folder> searchByMetadata(String searchParams, String tenantId) {
		logger.debug("Searching for contents with : " + searchParams);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.findAllContentsByMetadata(searchParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolders();
	}

	public List<String> folderIdBySharedGroup(String id, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		// String identityUrl = "http://localhost:8190";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(identityUrl + "/share/privateFoldersOnGroups" + "/" + id);
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request,
				String.class);
		String data = response.getBody();
		Gson gson = new Gson();
		String[] array = gson.fromJson(data, String[].class);
		List<String> folderlist = new ArrayList<String>(Arrays.asList(array));
		return folderlist;
	}

	public List<String> userIdByShared(String id, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		// String identityUrl = "http://localhost:8190";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(identityUrl + "/share/privateFolders" + "/" + id);

		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request,
				String.class);
		String data = response.getBody();
		Gson gson = new Gson();
		String[] array = gson.fromJson(data, String[].class);
		List<String> folderlist = new ArrayList<String>(Arrays.asList(array));
		return folderlist;
	}

	public List<String> userIdByGroup(String id, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		// String identityUrl = "http://localhost:8190";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(identityUrl + "/users/groupid" + "/" + id);

		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request,
				String.class);
		String data = response.getBody();
		Gson gson = new Gson();
		String[] array = gson.fromJson(data, String[].class);
		List<String> grouplist = new ArrayList<String>(Arrays.asList(array));

		return grouplist;
	}

	public List<Folder> findByPrivateFolderId(List<String> userIds, String tenantId) {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.findByPrivateFolderId(userIds);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolders();
	}

	public List<Folder> listFoldersUnderParentFolderIdInGroup(List<String> userIds, String tenantId) {
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.findByParentFolderIdInGroup(userIds, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolders();
	}

	public Folder getLastModifiedFolder(String parentFolderId, String tenantId) {
		logger.debug("Entering listFoldersForParentFolderId()");
		logger.debug("Finding folders with parentFolderId: " + parentFolderId);
		long startTime = System.nanoTime();
		InOutParameters inOutParameters = folderDao.getFolderByRevisedDateTime(parentFolderId, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "FolderService");
		return inOutParameters.getFolder();
	}

	/*
	 * @SuppressWarnings({ "rawtypes", "deprecation", "unused" }) public void
	 * markRecursiveFolder(String id, String tenantId) throws IOException { //
	 * List<Folder> folderList = listFoldersForParentFolderId(id, tenantId); //
	 * List<Content> contentList = listContentsUnderParentFolderId(id, tenantId);
	 * List<String> folderList = new ArrayList<String>(); folderList.add("");
	 * Map<String, Boolean> setData = new HashMap<String, Boolean>();
	 * setData.put("deleted", true); BasicDBObject updateSetValue = new
	 * BasicDBObject("$set", setData);
	 * 
	 * BasicDBObject inQuery = new BasicDBObject(); BasicDBObject inQuery1 = new
	 * BasicDBObject("$in", folderList); inQuery.put("id", inQuery1);
	 * 
	 * MongoConfig mongoConfig = new MongoConfig(); String database =
	 * mongoConfig.getDatabaseName(); MongoDatabase db = null; MongoCollection
	 * collection = null; UpdateOptions options = new UpdateOptions(); db =
	 * (MongoDatabase) new Mongo().getDB(database); collection =
	 * db.getCollection("folder");
	 * 
	 * UpdateResult updateResult = collection.updateMany(inQuery, updateSetValue,
	 * options); }
	 */

	public void callLoggingService(String tenantId, String userId, String logType, Long startTime, Long endTime,
			Double reqSize, Double resSize, String requestType, String serviceType) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("tenantId", tenantId);
		headers.set("userId", userId);
		headers.set("Content-Type", "application/json");

		String apiurl = loggingServiceUrl + "/logging/saveLog";
		if (reqSize != null) {
			reqSize = Math.ceil(reqSize);
		}
		if (resSize != null) {
			resSize = Math.ceil(resSize);
		}
		LogEntity logEntity = new LogEntity(logType, requestType, serviceType, reqSize, resSize, startTime, endTime);
		HttpEntity<LogEntity> request = new HttpEntity<LogEntity>(logEntity, headers);
		restTemplate.exchange(apiurl, HttpMethod.POST, request, String.class);
	}

}
