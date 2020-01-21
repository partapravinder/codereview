package com.newgen.service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.ContentDao;
import com.newgen.dao.FolderDao;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.ContentLocation;
import com.newgen.model.FilterItem;
import com.newgen.model.FilterItemCache;
import com.newgen.model.Folder;
import com.newgen.model.Lock;
import com.newgen.model.SearchResults;
import com.newgen.wrapper.service.WrapperAzureSearchService;
import com.newgen.wrapper.service.WrapperService;

@Service
public class ContentService extends ExceptionThrower {

	@Value("${storage.service.url}")
	private String storageUrl;

	private String storeApiPath = "/store";

	@Autowired
	ContentDao contentDao;

	@Autowired
	FolderDao folderDao;

	@Autowired
	ContentLocationService contentLocationService;

	@Autowired
	LockService lockService;

	@Autowired
	WrapperService service;

	@Autowired
	WrapperAzureSearchService azureSearchService;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

	public Content insert(Content content, String tenantId) throws CustomException {
		logger.debug("Creating content=> " + content);
		/*
		 * if (content.getId() == null) {
		 * content.setId(String.valueOf(sequenceDao.getNextSequenceId("documentindex")))
		 * ; }
		 */
		return contentDao.insert(content, tenantId);
	}

	public Content update(String id, String updateContentParams, BigDecimal version, String tenantId)
			throws CustomException {
		return update(id, updateContentParams, version, false, tenantId);
	}

	public Content checkOut(String id, String tenantId, String userId) throws CustomException {
		Content content = contentDao.findOne(id, tenantId);

		if (content.getCheckedOut()) {
			throwContentAlreadyCheckedOutException();
		}
		logger.debug("Updating Content with id: " + id);
		JSONObject json = new JSONObject();
		json.putOpt(Content.CHECKEDOUT_PARAM, true);
		json.putOpt(Content.CHECKEDOUTBY_PARAM, userId);
		Content content1 = contentDao.findAndCheckOut(id, json.toString(), tenantId);

		if (content1 == null) {
			throwContentNotFoundException();
		}

		return content1;
	}

	public Content setContentVersionLatest(String primaryContentId, BigDecimal version, String tenantId)
			throws CustomException {

		Content content = contentDao.findContentWithPrimaryContentId(primaryContentId, version, tenantId);
		if (content == null) {
			throwContentNotFoundException();
		} else if (content.getLatest() == true) {
			throwContentVersionAlreadyLatestException();
		}

		Content latestContent = contentDao.findLatestOne(primaryContentId, tenantId);

		if (latestContent != null && latestContent.getCheckedOut()) {
			throwContentCheckedOutException();
		}

		if (latestContent == null) {
			throwContentNotFoundException();
		} else if (latestContent.getLatest() == true && latestContent.getVersion() == version) {
			throwContentVersionAlreadyLatestException();
		}

		JSONObject updateContentParams = new JSONObject();
		updateContentParams.put(Content.LATEST_PARAM, false);
		latestContent = contentDao.findAndModify(latestContent.getId(), updateContentParams.toString(), null, false,
				tenantId, false);

		if (latestContent != null && latestContent.getLatest() == false) {
			updateContentParams.put(Content.LATEST_PARAM, true);
			content = contentDao.findAndModify(content.getId(), updateContentParams.toString(), version, false,
					tenantId, false);
		} else {
			throwUnknownErrorException();
		}

		if (content == null) {
			content = contentDao.findOne(primaryContentId, tenantId);
			if (content == null) {
				throwContentNotFoundException();
			} else {
				throwUnknownErrorException();
			}
		}

		return content;
	}

	public Content undoCheckOut(String id, String tenantId, String userId) throws CustomException {

		Content content = contentDao.findOne(id, tenantId);
		if (!content.getCheckedOut()) {
			throwContentNotCheckedOutException();
		}
		if (!content.getCheckedOutBy().equals(userId)) {
			throwContentNotCheckedOutByParameterUserException();
		}
		JSONObject json = new JSONObject();
		json.putOpt(Content.CHECKEDOUT_PARAM, false);
		json.putOpt(Content.CHECKEDOUTBY_PARAM, "");
		json.putOpt(Content.CHECKEDOUTTIME_PARAM, null);
		return update(id, json.toString(), false, tenantId);
	}

	public Content undoCheckOutAfterCheckIn(Content latestContent, String tenantId, String userId)
			throws CustomException {

		JSONObject json = new JSONObject();
		json.putOpt(Content.CHECKEDOUT_PARAM, false);
		json.putOpt(Content.CHECKEDOUTBY_PARAM, "");
		json.putOpt(Content.CHECKEDOUTTIME_PARAM, null);
		json.putOpt(Content.LATEST_PARAM, false);
		return update(latestContent.getId(), json.toString(), false, tenantId);

	}

	public Content update(String id, String updateContentParams, BigDecimal version, boolean ignoreCommittedFlag,
			String tenantId) throws CustomException {
		logger.debug("Updating Content with id: " + id + " and version: " + version);
		Content content = contentDao.findAndModify(id, updateContentParams, version, ignoreCommittedFlag, tenantId,
				false);

		if (content == null) {
			content = contentDao.findOne(id, tenantId);
			if (content == null) {
				throwContentNotFoundException();
			} else {
				if (version != null && content.getVersion() == version) {
					throwVersionConflictException();
				} else {
					throwUnknownErrorException();
				}
			}
		}
		return content;
	}

	public Content update(String id, String updateContentParams, boolean ignoreCommittedFlag, String tenantId)
			throws CustomException {
		logger.debug("Updating Content with id: " + id);
		// Content content = contentDao.findAndModify(id, updateContentParams, version,
		// ignoreCommittedFlag,tenantId);
		Content content = contentDao.findAndModify(id, updateContentParams, tenantId);

		if (content == null) {
			content = contentDao.findOne(id, tenantId);
			if (content == null) {
				throwContentNotFoundException();
			} else {
				throwUnknownErrorException();
			}
		}
		return content;
	}

	public void deleteContent(String id, BigDecimal version, String tenantId) throws CustomException {
		logger.debug("Deleting Content with id: " + id + " and version: " + version);
		if (version == null) {
			if (contentDao.findAndRemoveById(id, tenantId) == null) {
				throwContentNotFoundException();
			}
		} else {
			if (contentDao.findAndRemoveByIdAndVersion(id, version, tenantId) == null) {
				Content content = contentDao.findOne(id, tenantId);
				if (content == null) {
					throwContentNotFoundException();
				} else {
					if (content.getVersion() != version) {
						throwVersionConflictException();
					} else {
						throwUnknownErrorException();
					}
				}
			}
		}
	}

	public List<Content> search(Map<String, String[]> allRequestParams, String tenantId) {
		logger.debug("Searching for contents with : " + allRequestParams);
		return contentDao.findAllContents(allRequestParams, tenantId);
	}

	public List<Content> searchByPage(Map<String, String[]> allRequestParams, String tenantId, int pno) {
		logger.debug("Searching for contents with : " + allRequestParams);
		return contentDao.findAllContentsByPage(allRequestParams, tenantId, pno);
	}

	public Content findById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a content by id: " + id);
		return contentDao.findOne(id, tenantId);
	}

	public Content findLatestById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a content by id: " + id);
		return contentDao.findLatestOne(id, tenantId);
	}

	public Content findUncommitedById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a content by id: " + id);
		return contentDao.findUncommitedOne(id, tenantId);
	}

//	public Content findAllById(String id,String tenantId) throws CustomException {
//		logger.debug("Finding a content by id: " + id);
//		return contentDao.findEveryType(id,tenantId);
//	}

	public Folder findFolderById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a folder by id");
		return folderDao.findById(id, tenantId);
	}

	public List<Content> listContentsForParentFolderId(String parentFolderId, String tenantId) {
		logger.debug("Finding Contents with parentFolderId: " + parentFolderId);
		return contentDao.findByParentFolderId(parentFolderId, tenantId);
	}

	public List<Content> findByName(String name, String tenantId) {
		logger.debug("Finding Contents by name: " + name);
		return contentDao.findByName(name, tenantId);
	}

	public void markDeleteContent(String id, BigDecimal version, String tenantId) throws CustomException {
		logger.debug("Mark the content for delete with id: " + id + " and version: " + version);
		JSONObject deleteFlagParam = new JSONObject();
		deleteFlagParam.put("deleted", "true");
		BigDecimal versionValue = version == null ? null : version;
		update(id, deleteFlagParam.toString(), versionValue, tenantId);
	}

	public Content moveContent(String id, String targetFolderId, BigDecimal version, String tenantId)
			throws CustomException {
		logger.debug("Moving the content to: " + targetFolderId + " with id: " + id + " and version: " + version);
		Content content = contentDao.updateParentFolderId(id, targetFolderId, version, tenantId);
		if (content == null) {
			content = contentDao.findOne(id, tenantId);
			if (content == null) {
				throwContentNotFoundException();
			} else {
				if (version.compareTo(BigDecimal.valueOf(0.0)) > 0 && content.getVersion() != version) {
					throwVersionConflictException();
				} else {
					throwUnknownErrorException();
				}
			}
		}
		return content;
	}

	public List<Content> findAllDeletedContents() {
		logger.debug("Finding all contents marked for delete");
		return contentDao.findAllDeletedContents();
	}

	public void deleteContentLocationLink(String id, String tenantId) {
		logger.debug("Finding and remove content location from content with id: " + id);
		Content content = contentDao.findAndRemoveContentLocation(id, tenantId);
		if (content == null) {
			logger.debug("Content already deleted with id:" + id);
		}
	}

	public Content findContentWithContentLocation(String id, String tenantId) {
		return contentDao.findContentWithContentLocation(id, tenantId);
	}

	public void releaseLock(String id, String tenantId) throws Exception {
		lockService.delete(id, tenantId);
	}

	public Lock getExclusiveLock(String id, String guid, String tenantId) throws CustomException {
		return lockService.getLock(id, guid, "exclusive", tenantId);
	}

	public Lock getSharedLock(String id, String guid, String tenantId) throws CustomException {
		return lockService.getLock(id, guid, "shared", tenantId);
	}

	public Content copyContent(String parentFolderId, String id, String tenantId) throws CustomException {
		Content copiedContent = null;
		String lockId = null;
		String guid = UUID.randomUUID().toString();
		try {
			// Get Shared lock on parentFolderId
			lockId = parentFolderId;
			getSharedLock(lockId, guid, tenantId);

			// Check if the parent folder exists or not
			Folder folder = folderDao.findById(parentFolderId, tenantId);
			if (folder != null) {
				// Check if the content exists or not
				Content content = findById(id, tenantId);
				if (content != null) {

					// Increase count on content location
					ContentLocation contentLocation = contentLocationService
							.increaseCount(content.getContentLocationId(), tenantId);

					Content contentCopy = new Content(null, content.getName(), content.getContentType(),
							content.getComments(), folder.getId(), content.getOwnerName(), content.getOwnerId(),
							content.getContentLocationId(), new Date(), content.getRevisedDateTime(),
							content.getAccessDateTime(), content.getNoOfPages(), content.getDocumentType(),
							content.getDocumentSize(), content.getMetadata(), tenantId, content.getCheckedOut(), null,
							null, null, null, true, null, content.getVersion(), content.getDataclass());
					// TODO check if copied content would have a fresh version.
					// Create the copied content
					copiedContent = insert(contentCopy, tenantId);

					copiedContent.setContentLocationId(contentLocation.getId());
					copiedContent.setPrimaryContentId(copiedContent.getId());

					JSONObject updateContentParams = new JSONObject();
					updateContentParams.put("primaryContentId", content.getId());
					content = update(content.getId(), updateContentParams.toString(), true, tenantId);

				} else {
					logger.debug("Content not found with id: " + id);
					throwContentNotFoundException();
				}
			} else {
				logger.debug("ParentFolder not found with id: " + parentFolderId);
				throwParentFolderNotFoundException();
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
		return copiedContent;
	}

	public void handleCommitContent(String token, String storageId, String tenantId)
			throws CustomException, InterruptedException {
		// Get the content by the token
		Content content = contentDao.findByToken(token, tenantId);
		int retryCount = 5;
		while (content == null) {
			Thread.sleep(100);
			retryCount--;
			content = contentDao.findByToken(token, tenantId);
			if (retryCount <= 0)
				break;
		}
		if (content == null) {
			throwContentNotFoundException();
		} else {
			logger.debug("Content metadata to commit " + content.toString());

			// Update the content location with the storageId and mark the flag as
			// COMMITTED
			Map<String, String> contentLocationUpdateParams = new HashMap<>();
			contentLocationUpdateParams.put("locationId", storageId);
			contentLocationUpdateParams.put("flag", "COMMITTED");
			contentLocationService.update(content.getContentLocationId(), contentLocationUpdateParams, null, tenantId);

			// Mark the content as committed and remove the token
			// Map<String, String> updateContentParams = new HashMap<>();
			JSONObject updateContentParams = new JSONObject();

			updateContentParams.put("token", "");
			updateContentParams.put("flag", "COMMITTED");
			update(content.getId(), updateContentParams.toString(), null, true, tenantId);
			logger.debug("Acknowledging Storage service");
			// Acknowledge the storage service
			acknowledgeStorageService(token, tenantId);
		}
	}

	public void acknowledgeStorageService(String token, String tenantId) {
		service.acknowledgeStorageService(token, tenantId);
	}

	public void handleContentRemovalOnFailure(String token, String storageId, String tenantId) throws CustomException {
		// Find content location by storageId and remove it
		contentLocationService.findAndDeleteByLocationId(storageId, tenantId);

		// Find content and delete by token
		contentDao.findAndDeleteByToken(token, tenantId);
	}

	public List<Content> searchMetadata(String allRequestParams, String tenantId) {
		logger.debug("Searching for contents with : " + allRequestParams);
		return contentDao.findAllContentsByMetadata(allRequestParams, tenantId);
	}

	public List<Content> searchDataclass(String allRequestParams, String tenantId) {
		logger.debug("Searching for contents with : " + allRequestParams);
		return contentDao.findAllContentsByDataclass(allRequestParams, tenantId);
	}

	public SearchResults fullTextSearch(Map<String, Set<Object>> allRequestParams, String tenantId) throws CustomException, JsonProcessingException, ParseException {
		SearchResults searchResults = new SearchResults();
		logger.debug("Searching for contents with : " + allRequestParams);
		String searchString = allRequestParams.get("name").iterator().next().toString();
		searchString = createRegexforSearch(searchString, false);
		Set<Object> searchStringSet = new HashSet<Object>();
		searchStringSet.add(searchString);
		allRequestParams.put("name", searchStringSet);
		if(allRequestParams.get("assetType").contains("folder")) {
			searchResults.setFolder(contentDao.findAllFolders(allRequestParams, tenantId));
		}
		if(allRequestParams.get("assetType").contains("document")) {
			searchResults.setDocument(contentDao.findAllContentsBySearchString(allRequestParams, tenantId, false));
		}
		if(allRequestParams.get("assetType").contains("fts")) {
			// Algorithm
			// Obtain Indexer
			// Search the text string from azure search
			// From the urls obtained from azure search, search storage location collection and get storage location id's
			// From storage location ids, search content location collection and get content location id's
			// From content location ids, search for documents
			// Add document extracted content into search document result set(requires creating hashmap earlier)
			
			// Get Indexer for tenantID
			//String indexer = "searchIndexer";
			if(allRequestParams.get("searchString").size() > 0) {
				searchString = allRequestParams.get("searchString").iterator().next().toString();
				if(!StringUtils.isEmpty(searchString)) 
				{	
					searchString = createRegexforSearch(searchString, true);
					HashMap<String,String> searchedDocuments = azureSearchService.searchPlus(searchString, tenantId);
					if(searchedDocuments == null) {
						searchResults.setFts(new ArrayList<Content>());
					} else {
						List<String> documentUris = new ArrayList<String>(searchedDocuments.keySet());
						
						ObjectMapper mapper = new ObjectMapper();
						HttpHeaders headers = new HttpHeaders();
						headers.set("tenantId", tenantId);
						headers.setContentType(MediaType.APPLICATION_JSON);
						ResponseEntity<String> response;
				
						HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(documentUris), headers);
						response = restTemplate.exchange(storageUrl + storeApiPath + "/search/storage/ids", HttpMethod.POST, request, String.class);
				
						JsonReader jsonReader = Json.createReader(new StringReader(response.getBody()));
				        JsonArray jsonArray = jsonReader.readObject().getJsonArray("storageLocationList");
				        HashMap<String, String> storageLocationMap = parseStorageLocationIds(jsonArray);
				        
				        List<String> storageLoctionIds = new ArrayList<String>(storageLocationMap.keySet());
						List<ContentLocation> contentLocations = contentLocationService.findByLocationIds(storageLoctionIds, tenantId);
						HashMap<String, String> contentLocationMap = new HashMap<String, String>();
						Set<Object> contentLocationIds = new HashSet<Object>();
						Iterator<ContentLocation> itr = contentLocations.iterator();
						for(;itr.hasNext();) {
							ContentLocation contentLocation = itr.next();
							contentLocationIds.add(contentLocation.getId());
							contentLocationMap.put(contentLocation.getId(), contentLocation.getLocationId());
							storageLocationMap.put(contentLocation.getId(), contentLocation.getLocationId());
						}
						//Search from Azure Search and get Document URLs
						allRequestParams.put("contentLocationId", contentLocationIds);
						List<Content> contents =  contentDao.findAllContentsBySearchString(allRequestParams, tenantId, true);
						Iterator<Content> itr1 = contents.iterator();
						for(;itr1.hasNext();) {
							Content content = itr1.next();
							content.setContent("");
							String contentLocationId = content.getContentLocationId();
							String storageLocatioId = contentLocationMap.get(contentLocationId);
							if(storageLocatioId != null) {
								String documentUri = storageLocationMap.get(storageLocatioId);
								if(documentUri != null) {
									String strContent = searchedDocuments.get(documentUri);
									int index = strContent.toLowerCase().indexOf(searchString.toLowerCase());
									if(index >= 0) {
										if(index + 150 < strContent.length()) {
											strContent = strContent.substring(index, index + 150);
										} else {
											strContent = strContent.substring(index);
										}
										content.setContent(strContent);
									}
								}
							}
							//content.setContent(searchedDocuments.get(content.get));
						}
						searchResults.setFts(contents);
					}
				}
			}
		}
		return processSearchResults(tenantId, allRequestParams, searchResults);
	}
	
	private SearchResults processSearchResults(String tenantId, Map<String, Set<Object>> allRequestParams, SearchResults searchResults) {
		
		HashMap<String, Content> asset = new HashMap<String, Content>();
		HashMap<String, FilterItem> owner = new HashMap<String, FilterItem>();
		HashMap<String, FilterItem> documentType = new HashMap<String, FilterItem>();
		HashMap<String, FilterItem> keyword = new HashMap<String, FilterItem>();
		HashMap<String, FilterItemCache> dataClass = new HashMap<String, FilterItemCache>();
		Set<String> filterDocumentType = convertObjectSetToStringSet(allRequestParams.get("documentType"));
		Set<String> filterOwnerId = convertObjectSetToStringSet(allRequestParams.get("ownerId"));
		Set<String> filterKeyword = convertObjectSetToStringSet(allRequestParams.get("keyword"));
		Set<FilterItem> filterDataClass = convertObjectSetToFilterItemSet(allRequestParams.get("dataclass"));
		
		// Handle parentFolders
		HashMap<String,String> parentFoldersMap = new HashMap<String, String>();
		if(searchResults.getDocument() != null) {
			searchResults.getDocument().stream().forEach(content ->{						
				parentFoldersMap.put(content.getParentFolderId(), "");
			});
		}
		if(searchResults.getFolder() != null) {
			searchResults.getFolder().stream().forEach(folder ->{
				parentFoldersMap.put(folder.getParentFolderId(), "");
			});
		}
		if(searchResults.getFts() != null) {
			searchResults.getFts().stream().forEach(content ->{
				parentFoldersMap.put(content.getParentFolderId(), "");
			});
		}
				
		List<String> folderIds = new ArrayList<String>(parentFoldersMap.keySet());
		List<Folder> parentFolders = contentDao.findAllFolders(folderIds, tenantId);
		parentFolders.stream().forEach(folder -> {
			parentFoldersMap.put(folder.getId(), folder.getFolderName());
		});
				
				
		//Apply filters
		if(searchResults.getFts() != null) {
			Iterator<Content> itr = searchResults.getFts().iterator();
			while (itr.hasNext()) {
			   Content content = itr.next();
				String parentFolderName = parentFoldersMap.get(content.getParentFolderId());
				if(parentFolderName.compareToIgnoreCase("TenantAssetFolder") == 0) {
					content.setParentFolderName("Root");
					if(!asset.containsKey(content.getId())){
						processFilters(asset, owner, documentType, keyword, dataClass, filterDocumentType, filterOwnerId, filterKeyword, filterDataClass, content, null);
					}
				} else if(parentFolderName.compareToIgnoreCase("TenantActivityFolder") == 0 || 
						parentFolderName.compareToIgnoreCase("TenantCasesFolder") == 0 || 
						parentFolderName.compareToIgnoreCase("TenantCaseTypesFolder") == 0 || 
						parentFolderName.compareToIgnoreCase("TenantTempFolder") == 0) {
					itr.remove();
				} else {
					content.setParentFolderName(parentFolderName);
					if(!asset.containsKey(content.getId())){
						processFilters(asset, owner, documentType, keyword, dataClass, filterDocumentType, filterOwnerId, filterKeyword, filterDataClass, content, null);
					}
				}
			}
		}
		
		if(searchResults.getDocument() != null) {
			Iterator<Content> itr = searchResults.getDocument().iterator();
			while (itr.hasNext()) {
			   Content content = itr.next();
			   String parentFolderName = parentFoldersMap.get(content.getParentFolderId());
			   if(parentFolderName.compareToIgnoreCase("TenantAssetFolder") == 0) {
				   content.setParentFolderName("Root");
				   if(!asset.containsKey(content.getId())){
					   processFilters(asset, owner, documentType, keyword, dataClass, filterDocumentType, filterOwnerId, filterKeyword, filterDataClass, content, null);
				   }
			   } else if(parentFolderName.compareToIgnoreCase("TenantActivityFolder") == 0 || 
					   parentFolderName.compareToIgnoreCase("TenantCasesFolder") == 0 || 
					   parentFolderName.compareToIgnoreCase("TenantCaseTypesFolder") == 0 ||
					   parentFolderName.compareToIgnoreCase("TenantTempFolder") == 0) {
				   itr.remove();
			   } else {
				   content.setParentFolderName(parentFolderName);	
				   if(!asset.containsKey(content.getId())){
					   processFilters(asset, owner, documentType, keyword, dataClass, filterDocumentType, filterOwnerId, filterKeyword, filterDataClass, content, null);
				   }
			   }
			}
		}
		if(searchResults.getFolder() != null) {
			Iterator<Folder> itr = searchResults.getFolder().iterator();
			HashMap<String,String> folderMapForDuplicateCheck = new HashMap<String, String>();
			while (itr.hasNext()) {
				Folder folder = itr.next();
				if(folderMapForDuplicateCheck.containsKey(folder.getId())) {
					itr.remove();
					continue;
				} else {
					folderMapForDuplicateCheck.put(folder.getId(), "");
				}
				String parentFolderName = parentFoldersMap.get(folder.getParentFolderId());
				if(parentFolderName.compareToIgnoreCase("TenantActivityFolder") == 0 || 
						parentFolderName.compareToIgnoreCase("TenantCasesFolder") == 0 || 
						parentFolderName.compareToIgnoreCase("TenantCaseTypesFolder") == 0 || 
						parentFolderName.compareToIgnoreCase("TenantTempFolder") == 0) {
					itr.remove();
				} else {
					if(parentFolderName.compareToIgnoreCase("TenantAssetFolder") == 0) {
						folder.setParentFolderName("Root");
					} else {
						folder.setParentFolderName(parentFolderName);
					}
					Content content = new Content();
					content.setId(folder.getId());
					content.setName(folder.getFolderName());
					content.setParentFolderId(folder.getParentFolderId());
					content.setCreationDateTime(folder.getCreationDateTime());
					content.setOwnerName(folder.getOwnerName());
					// content.setPrivilege(folder.getPrivilege());
					content.setRevisedDateTime(folder.getRevisedDateTime());
					if(!processFilters(asset, owner, documentType, keyword, dataClass, filterDocumentType, filterOwnerId, filterKeyword, filterDataClass, null, folder)) {
						itr.remove();
					}
				}	
			}
		}
		searchResults.setFts(null);
		if(asset.size() > 0) {
			searchResults.setDocument(new ArrayList<Content>(asset.values()));
		} else {
			searchResults.setDocument(new ArrayList<Content>());
		}
		if(keyword.size() > 0) {
			searchResults.setKeyword(new ArrayList<FilterItem>(keyword.values()));
		} else {
			searchResults.setKeyword(new ArrayList<FilterItem>());
		}
		
		ArrayList<FilterItem> dataclass = new ArrayList<FilterItem>();
		if(filterDataClass.size() > 0) {
			searchResults.setDataclass((ArrayList)convertObjectSetToFilterItemList(allRequestParams.get("dataclass")));
		} else {
			Iterator<FilterItemCache> itr = dataClass.values().iterator();
			while(itr.hasNext()) {
				dataclass.add(convertDataClassFilterItemCacheToFilterItem(itr.next()));
			}
			searchResults.setDataclass(dataclass);
		}
 
		if(documentType.size() > 0) {
			searchResults.setDocumentType(new ArrayList<FilterItem>(documentType.values()));
		} else {
			searchResults.setDocumentType(new ArrayList<FilterItem>());
		}
		if(owner.size() > 0) {
			searchResults.setOwner(new ArrayList<FilterItem>(owner.values()));
		} else {
			searchResults.setOwner(new ArrayList<FilterItem>());
		}
		/*
		 * searchResults.setDocument(null); searchResults.setFts(null);
		 */
		return searchResults;
	}
	
	private boolean processFilters(HashMap<String, Content> asset, 
			HashMap<String, FilterItem> owner, HashMap<String, FilterItem> documentType, 
			HashMap<String, FilterItem> keyword, HashMap<String, FilterItemCache> dataClass,
			Set<String> filterDocumentType, Set<String> filterOwnerId, 
			Set<String> filterKeyword, Set<FilterItem> filterDataClass, Content content, Folder folder) {
		String tmpOwnerId;
		String tmpOwnerName;
		if(content != null) {
			tmpOwnerId = content.getOwnerId();
			tmpOwnerName = content.getOwnerName();
		} else {
			tmpOwnerId = folder.getOwnerId();
			tmpOwnerName = folder.getOwnerName();
		}
		FilterItem filterItem = owner.get(tmpOwnerId);
		if(filterItem == null) {
			owner.put(tmpOwnerId, new FilterItem(tmpOwnerId, tmpOwnerName, 1));
		} else {
			filterItem.setCount(filterItem.getCount() + 1);
		}
		
		String tmpDocumentType = null;
		if(content != null) { 
			tmpDocumentType = content.getDocumentType();
			if(tmpDocumentType == null || tmpDocumentType.trim().compareTo("") == 0) {
				tmpDocumentType = "";
			} else {	
				tmpDocumentType = tmpDocumentType.toLowerCase().trim();
			}
			filterItem = documentType.get(tmpDocumentType);
			if(filterItem == null) {
				documentType.put(tmpDocumentType, new FilterItem(tmpDocumentType, tmpDocumentType, 1));
			} else {
				filterItem.setCount(filterItem.getCount() + 1);
			}						
		}
		// TODO - implement keyword and dataclass
		String tmpKeyword = null;
		String []tmpKeywords = null;
		Map<String, String> metaData;
		if(content != null) {
			metaData = content.getMetadata();
		} else {
			metaData = folder.getMetadata();
		}
		if(metaData != null) { 
			tmpKeyword = metaData.get("keywords");
			if(tmpKeyword == null) {
				tmpKeyword = "";
			}
			if(tmpKeyword.trim().compareTo("") == 0 || !tmpKeyword.contains(",")) {
				tmpKeywords = new String[1];
				tmpKeywords[0] = tmpKeyword;
			} else {	
				tmpKeywords = tmpKeyword.split(",");
			}
			for(int i = 0; i < tmpKeywords.length; i++) {
				tmpKeyword = tmpKeywords[i].toLowerCase().trim();
				tmpKeywords[i] = tmpKeyword;
				if(tmpKeyword.equals("")) {
					continue;
				}
				filterItem = keyword.get(tmpKeyword);
				if(filterItem == null) {
					keyword.put(tmpKeyword, new FilterItem(tmpKeyword, tmpKeyword, 1));
				} else {
					filterItem.setCount(filterItem.getCount() + 1);
				}		
			}				
		}
		String tmpDataClass = null;
		String tmpDataClassId = null;
		Map<String, String> dataClassObj;
		if (content != null) {
			dataClassObj = content.getDataclass();
		} else {
			dataClassObj = folder.getDataclass();
		}
		if (dataClassObj != null) {
			tmpDataClass = dataClassObj.get("dataClassName");
			tmpDataClassId = dataClassObj.get("id");
			if (tmpDataClass == null || tmpDataClass.trim().compareTo("") == 0) {
				tmpDataClass = "";
			} else {
				tmpDataClass = tmpDataClass.toLowerCase().trim();
			}
			FilterItemCache dataClassFilterItemCache = dataClass.get(tmpDataClassId);
			if (dataClassFilterItemCache == null) {
				dataClassFilterItemCache = convertDataClassToFilterItemCache(dataClassObj);
				if(dataClassFilterItemCache.getValue() != null && !dataClassFilterItemCache.getValue().trim().equals("")) {
					dataClass.put(tmpDataClassId, dataClassFilterItemCache);
				}
			} else {
				updateDataClassInFilterItemCache(dataClassObj, dataClassFilterItemCache);
			}
		}  
		
		boolean valid = false; 
		if((filterDocumentType == null || filterDocumentType.size() == 0) && 
				(filterOwnerId == null || filterOwnerId.size() == 0) &&
				(filterKeyword == null || filterKeyword.size() == 0)) {
			if(filterDataClass == null || filterDataClass.size() == 0) {
				valid = true;
			} else {
				boolean dataClassCheck = false;
				Iterator<FilterItem> dataClassItr = filterDataClass.iterator();
				while(dataClassItr.hasNext()) {
					FilterItem dataClassFilterItem = dataClassItr.next();
					if(dataClassFilterItem.getChecked()) {
						dataClassCheck = true;
						break;
					}
					Iterator<FilterItem> fieldItr = dataClassFilterItem.getFields().iterator();
					while(fieldItr.hasNext()) {
						FilterItem fieldFilterItem = fieldItr.next();
						Iterator<FilterItem> valueItr = fieldFilterItem.getFields().iterator();
						while(valueItr.hasNext()) {
							FilterItem valueFilterItem = valueItr.next();
							if(valueFilterItem.getChecked()) {
								dataClassCheck = true;
								break;
							}
						}
						if(dataClassCheck)
							break;
					}
				}
				if(dataClassCheck) {
					valid = false;
				} else {
					valid = true;
				}
				if(valid) {
					if(content != null) 
						asset.put(content.getId(), content);
					return valid;
				}
			}		
		}
		if(filterDocumentType != null && filterDocumentType.size() > 0 && filterDocumentType.contains(tmpDocumentType)) {
			valid = true;
		}
		if(filterOwnerId != null && filterOwnerId.size() > 0 && filterOwnerId.contains(tmpOwnerId)) {
			valid = true;
		}
		if(filterKeyword != null && filterKeyword.size() > 0 && tmpKeywords != null) {
			for(int i = 0; i < tmpKeywords.length; i++) {
				tmpKeyword = tmpKeywords[i].toLowerCase().trim();
				if(filterKeyword.contains(tmpKeyword) && !tmpKeyword.equals("")) {
					valid = true;
					break;
				}
			}	
		}
		if(filterDataClass != null && filterDataClass.size() > 0) {
			if(tmpDataClass != null) {
				Iterator<FilterItem> dataClassItr = filterDataClass.iterator();
				while(dataClassItr.hasNext()) {
					FilterItem dataClassFilterItem = dataClassItr.next();
					if(tmpDataClass.compareToIgnoreCase(dataClassFilterItem.getValue()) == 0) {
						if(dataClassFilterItem.getChecked()) {
							valid = true;
							break;
						}
						Iterator<FilterItem> fieldItr = dataClassFilterItem.getFields().iterator();
						while(fieldItr.hasNext()) {
							FilterItem fieldFilterItem = fieldItr.next();
							if(dataClassObj.containsKey(fieldFilterItem.getValue())) {
								String dataClassFieldValue = dataClassObj.get(fieldFilterItem.getValue());
								Iterator<FilterItem> valueItr = fieldFilterItem.getFields().iterator();
								while(valueItr.hasNext()) {
									FilterItem valueFilterItem = valueItr.next();
									if(valueFilterItem.getChecked() && valueFilterItem.getValue().compareToIgnoreCase(dataClassFieldValue) == 0) {
										valid = true;
									}
								}
							}
						}
					}
				}
			}
		}		
		if(content != null && valid)
			asset.put(content.getId(), content);
		return valid;
	}

	private HashMap<String, String> parseStorageLocationIds(JsonArray jsonArray) {
		HashMap<String, String> storageLocationMap = new HashMap<String, String>();
		int resultsCount = jsonArray.size();
		String key;
		String value;
		for (int i = 0; i <= resultsCount - 1; i++) {
			JsonObject jsonObject = jsonArray.getJsonObject(i);
			key = jsonObject.getString("id");
			value = jsonObject.getString("blobUri");
			storageLocationMap.put(key, value);
		}
		return storageLocationMap;
	}

	public Content getLastModifiedContent(String parentFolderId, String tenantId) {
		logger.debug("Entering contentsForParentFolderId()");
		logger.debug("Finding contents with parentFolderId: " + parentFolderId);
		return contentDao.getContentByRevisedDateTime(parentFolderId, tenantId);
	}
	
	private Set<String> convertObjectSetToStringSet(Set<Object> props) {
		Set<String> keys = new HashSet<>();
		if(props != null && !props.isEmpty()) {
			Iterator<Object> itr = props.iterator();
			while (itr.hasNext()) {
	    		Object o = itr.next();
	        	keys.add((String) o);
	    	}
	    }
	    return keys;
	}
	
	private Set<FilterItem> convertObjectSetToFilterItemSet(Set<Object> props) {
		Set<FilterItem> keys = new HashSet<>();
		if(props != null && !props.isEmpty()) {
			Iterator<Object> itr = props.iterator();
			while (itr.hasNext()) {
	    		Object o = itr.next();
	    		HashMap<String, Object> dataClassMap = (HashMap<String, Object>)o; 
	        	FilterItem dataClassFilterItem = new FilterItem();
	    		dataClassFilterItem.setValue((String)dataClassMap.get("value"));
	    		Object checked = dataClassMap.get("checked");
	    		if(checked == null) {
	    			dataClassFilterItem.setChecked(false);
	    		} else {
	    			dataClassFilterItem.setChecked((boolean)checked);
	    		}
	    		List<FilterItem> fieldFilterItemList = new ArrayList<FilterItem>();
	    		ArrayList<HashMap<String, Object>> fieldList = (ArrayList<HashMap<String, Object>>)dataClassMap.get("fields");
	    		Iterator<HashMap<String, Object>> fieldItr = fieldList.iterator();
	    		while(fieldItr.hasNext()) {
	    			HashMap<String, Object> fieldMap = fieldItr.next();
	    			FilterItem fieldFilterItem = new FilterItem();
		    		fieldFilterItem.setValue((String)fieldMap.get("value"));
		    		List<FilterItem> valueFilterItemList = new ArrayList<FilterItem>();
		    		ArrayList<HashMap<String, Object>> valueList = (ArrayList<HashMap<String, Object>>)fieldMap.get("fields");
		    		Iterator<HashMap<String, Object>> valueItr = valueList.iterator();
		    		while(valueItr.hasNext()) {
		    			HashMap<String, Object> valueMap = valueItr.next();
		    			FilterItem valueFilterItem = new FilterItem();
			    		valueFilterItem.setValue((String)valueMap.get("value"));
			    		checked = valueMap.get("checked");
			    		if(checked == null) {
			    			valueFilterItem.setChecked(false);
			    		} else {
			    			valueFilterItem.setChecked((boolean)checked);
				    	}
			    		valueFilterItemList.add(valueFilterItem);
		    		}
		    		fieldFilterItem.setFields(valueFilterItemList);
		    		fieldFilterItemList.add(fieldFilterItem);
	    		}
	    		dataClassFilterItem.setFields(fieldFilterItemList);
	    		keys.add(dataClassFilterItem);
	    	}
	    }
	    return keys;
	}
	
	private List<FilterItem> convertObjectSetToFilterItemList(Set<Object> props) {
		ArrayList<FilterItem> keys = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			Iterator<Object> itr = props.iterator();
			while (itr.hasNext()) {
	    		Object o = itr.next();
	    		HashMap<String, Object> dataClassMap = (HashMap<String, Object>)o; 
	        	FilterItem dataClassFilterItem = new FilterItem();
	    		dataClassFilterItem.setValue((String)dataClassMap.get("value"));
	    		dataClassFilterItem.setId((String)dataClassMap.get("id"));
	    		dataClassFilterItem.setCount((Integer)dataClassMap.get("count"));
	    		Object checked = dataClassMap.get("checked");
	    		if(checked == null) {
	    			dataClassFilterItem.setChecked(false);
	    		} else {
	    			dataClassFilterItem.setChecked((boolean)checked);
	    		}
	    		List<FilterItem> fieldFilterItemList = new ArrayList<FilterItem>();
	    		ArrayList<HashMap<String, Object>> fieldList = (ArrayList<HashMap<String, Object>>)dataClassMap.get("fields");
	    		Iterator<HashMap<String, Object>> fieldItr = fieldList.iterator();
	    		while(fieldItr.hasNext()) {
	    			HashMap<String, Object> fieldMap = fieldItr.next();
	    			FilterItem fieldFilterItem = new FilterItem();
		    		fieldFilterItem.setValue((String)fieldMap.get("value"));
		    		fieldFilterItem.setId((String)fieldMap.get("id"));
		    		fieldFilterItem.setCount((Integer)fieldMap.get("count"));
		    		List<FilterItem> valueFilterItemList = new ArrayList<FilterItem>();
		    		ArrayList<HashMap<String, Object>> valueList = (ArrayList<HashMap<String, Object>>)fieldMap.get("fields");
		    		Iterator<HashMap<String, Object>> valueItr = valueList.iterator();
		    		while(valueItr.hasNext()) {
		    			HashMap<String, Object> valueMap = valueItr.next();
		    			FilterItem valueFilterItem = new FilterItem();
			    		valueFilterItem.setValue((String)valueMap.get("value"));
			    		valueFilterItem.setId((String)valueMap.get("id"));
			    		valueFilterItem.setCount((Integer)valueMap.get("count"));
			    		checked = valueMap.get("checked");
			    		if(checked == null) {
			    			valueFilterItem.setChecked(false);
			    		} else {
			    			valueFilterItem.setChecked((boolean)checked);
				    	}
			    		valueFilterItemList.add(valueFilterItem);
		    		}
		    		fieldFilterItem.setFields(valueFilterItemList);
		    		fieldFilterItemList.add(fieldFilterItem);
	    		}
	    		dataClassFilterItem.setFields(fieldFilterItemList);
	    		keys.add(dataClassFilterItem);
	    	}
	    }
	    return keys;
	}
	
	private FilterItemCache convertDataClassToFilterItemCache(Map<String, String> dataClassObj) {
		FilterItemCache dataClassFilterItemCache = new FilterItemCache();
		Iterator<Entry<String, String>> itr = dataClassObj.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			String key = entry.getKey(); 
			String value = entry.getValue();
			if(key.equals("id")) {
				dataClassFilterItemCache.setId(value);
			} else if(key.equals("dataClassName")) {
				dataClassFilterItemCache.setValue(value);
			} else {
				if(!StringUtils.isEmpty(value)) {
					FilterItemCache fieldFilterItemCache = new FilterItemCache(key, key, 1, new HashMap<String, FilterItemCache>());
					FilterItemCache valueFilterItemCache = new FilterItemCache(value,value, 1, null);
					fieldFilterItemCache.getSubItems().put(value, valueFilterItemCache);
					dataClassFilterItemCache.getSubItems().put(key, fieldFilterItemCache);
				}
			}
			dataClassFilterItemCache.setCount(1);
		}
		return dataClassFilterItemCache;
	}
	
	private FilterItemCache updateDataClassInFilterItemCache(Map<String, String> dataClassObj, FilterItemCache dataClassFilterItemCache) {
		dataClassFilterItemCache.setCount(dataClassFilterItemCache.getCount() + 1);
		Iterator<Entry<String, String>> itr = dataClassObj.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, String> entry = itr.next();
			String key = entry.getKey(); 
			String value = entry.getValue();
			if(key.compareToIgnoreCase("id") != 0 && key.compareToIgnoreCase("dataClassName") != 0) {
				if(StringUtils.isEmpty(value)) {
					continue;
				}
				FilterItemCache fieldFilterItemCache = dataClassFilterItemCache.getSubItems().get(key);
				if(fieldFilterItemCache == null) {
					fieldFilterItemCache = new FilterItemCache(key, key, 1, new HashMap<String, FilterItemCache>());
					FilterItemCache valueFilterItemCache = new FilterItemCache(value,value, 1, null);
					fieldFilterItemCache.getSubItems().put(value, valueFilterItemCache);
					dataClassFilterItemCache.getSubItems().put(key, fieldFilterItemCache);
				} else {
					fieldFilterItemCache.setCount(fieldFilterItemCache.getCount() + 1);
					FilterItemCache valueFilterItemCache = fieldFilterItemCache.getSubItems().get(value);
					if(valueFilterItemCache == null) {
						valueFilterItemCache = new FilterItemCache(value,value, 1, null);
						fieldFilterItemCache.getSubItems().put(value, valueFilterItemCache);
					} else {
						valueFilterItemCache.setCount(valueFilterItemCache.getCount() + 1);
					}
				}
			}
		}
		return dataClassFilterItemCache;
	}
	
	private FilterItem convertDataClassFilterItemCacheToFilterItem(FilterItemCache dataClassFilterItemCache) {
		FilterItem filterItem = new FilterItem(dataClassFilterItemCache.getId(), 
				dataClassFilterItemCache.getValue(), dataClassFilterItemCache.getCount());
		Iterator<FilterItemCache> itr = dataClassFilterItemCache.getSubItems().values().iterator();
		while(itr.hasNext()) {
			FilterItemCache fieldFilterItemCache = itr.next();
			filterItem.getFields().add(convertFieldFilterItemCacheToFilterItem(fieldFilterItemCache));
		}
		return filterItem;
	}
	
	private FilterItem convertFieldFilterItemCacheToFilterItem(FilterItemCache fieldFilterItemCache) {
		FilterItem filterItem = new FilterItem(fieldFilterItemCache.getId(),
				fieldFilterItemCache.getValue(), fieldFilterItemCache.getCount());
		Iterator<FilterItemCache> itr = fieldFilterItemCache.getSubItems().values().iterator();
		while(itr.hasNext()) {
			FilterItemCache valueFilterItemCache = itr.next();
			filterItem.getFields().add(convertValueFilterItemCacheToFilterItem(valueFilterItemCache));
		}
		return filterItem;
	}
	
	private FilterItem convertValueFilterItemCacheToFilterItem(FilterItemCache valueFilterItemCache) {
		return new FilterItem(valueFilterItemCache.getId(), 
				valueFilterItemCache.getValue(),valueFilterItemCache.getCount());
	}
	
	private String createRegexforSearch(String searchString, boolean isFTSEnabled) {
		boolean isMultiValue = false;
		if(StringUtils.isEmpty(searchString)) {
			return "";
		}
		searchString = searchString.replaceAll("\\*", " ");
		searchString = searchString.replaceAll("\\?", " ");
		searchString = searchString.replaceAll("\\(", " ");
		searchString = searchString.replaceAll("\\)", " ");
		searchString = searchString.replaceAll(",", " ");
		searchString = searchString.toLowerCase();
		//searchString = searchString.replaceAll("or", " ");
		//searchString = searchString.replaceAll("and", " ");
		searchString = searchString.trim();
		if(searchString.contains(" ")) {
			String[] searchSubStrings = searchString.split(" ");
			searchString = "";
			for(String searchSubString : searchSubStrings) {
				if(searchSubString.trim().compareToIgnoreCase("") == 0) {
					continue;
				}
				if(isMultiValue) {
					searchString += "|" + searchSubString;
					if(isFTSEnabled) {
						searchString += "*";
					}
				} else {
					searchString += searchSubString;
					if(isFTSEnabled) {
						searchString += "*";
					}
					isMultiValue = true;										
				}
			}
		}
		if(!isFTSEnabled && searchString.contains("|")) {
			searchString = "(" + searchString + ")";
		}
		return searchString;
	}
}
