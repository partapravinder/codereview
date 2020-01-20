package com.newgen.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.newgen.controller.ExceptionThrower;
//import com.newgen.dao.ContentDao;
import com.newgen.dao.FolderDao;
import com.newgen.exception.CustomException;
//import com.newgen.model.Content;
import com.newgen.model.Folder;

@Service
public class FolderService extends ExceptionThrower {

	private static final Logger logger = LoggerFactory.getLogger(FolderService.class);

	@Autowired
	private RestTemplate restTemplate;

//	@Autowired
//  private EurekaUrlResolver eurekaUrlResolver;
	
//    @Value("${service.folder.serviceId}")
//    private String folderServiceId;
    
    @Value("${folder.service.url}")
    private String folderServiceUrl;
    
	@Autowired
	FolderDao folderDao;

	public Folder insert(Folder folder) throws CustomException {
		logger.debug("Creating Folder");
		return folderDao.insert(folder);
	}

	public Folder findById(String id, String tenantId) throws CustomException {
		logger.debug("Finding a folder by id");
		return folderDao.findById(id, tenantId);
	}

	public List<Folder> search(Map<String, String[]> allRequestParams, String tenantId) throws CustomException {
		logger.debug("Searching for folders based on : " + allRequestParams);
		return folderDao.findAllFolders(allRequestParams, tenantId);
	}

	public List<Folder> searchByPage(Map<String, String[]> allRequestParams, String tenantId, int pno)
			throws CustomException {
		logger.debug("Searching for folders based on : " + allRequestParams);
		return folderDao.findAllFoldersByPage(allRequestParams, tenantId, pno);
	}

	public void delete(String id, String version, String tenantId) throws CustomException {
		if (version == null || version.isEmpty()) {
			if (folderDao.findAndRemoveById(id, tenantId) == null) {

				throwFolderNotFoundException();
			}
		} else {
			if (folderDao.findAndRemoveByIdAndVersion(id, version, tenantId) == null) {
				Folder folder = folderDao.findById(id, tenantId);
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

	public boolean isFolderEmpty(String id, String tenantId) throws CustomException {
		// Check if the id passed exists as a parentFolderId for any content
		// folder
		logger.debug("Entering isFolderEmpty()");
		logger.debug("Finding folders with parentFolderId: " + id);

		List<Folder> folderList = listFoldersUnderParentFolderId(id, tenantId);
		if (folderList.size() != 0) {
			return false;
		}
		logger.debug("Finding content with parentFolderId: " + id);
		logger.debug("Folders/Content not found!");
		return true;
	}

	public List<Folder> listFoldersUnderParentFolderId(String id, String tenantId) {
		return folderDao.findByParentFolderId(id, tenantId);
	}

	public List<Folder> listFoldersForParentFolderId(String parentFolderId, String tenantId) {
		logger.debug("Entering listFoldersForParentFolderId()");
		logger.debug("Finding folders with parentFolderId: " + parentFolderId);
		return folderDao.findByParentFolderId(parentFolderId, tenantId);
	}

	public Folder update(String id, Map<String, String> updateFolderParams, Long version, String tenantId)
			throws CustomException {
		Folder folder = folderDao.findAndModify(id, updateFolderParams, version, tenantId);

			if (folder == null) {
				folder = folderDao.findById(id,tenantId);
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

	public Folder moveFolder(String id, String targetId, Long version, String tenantId) throws CustomException {
		Folder folder = folderDao.updateParentFolderId(id, targetId, version, tenantId);
		if (folder == null) {
			folder = folderDao.findById(id, tenantId);
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

	public void markDeleteFolder(String id, String version, String tenantId) throws CustomException {
		logger.debug("Mark the folder for delete with id: " + id + " and version: " + version);
		Map<String, String> deleteFlagParam = new HashMap<>();
		deleteFlagParam.put("deleted", "true");
		Long versionValue = version == null ? null : Long.valueOf(version);
		update(id, deleteFlagParam, versionValue, tenantId);
	}

	public void deleteFolderAndChildren(String id, String tenantId) throws CustomException {

		markDeleteFolder(id, null, tenantId);
		// Iterate through folders and delete the same
		List<Folder> folderList = listFoldersUnderParentFolderId(id, tenantId);
		for (Folder folderObj : folderList) {
			deleteFolderAndChildren(folderObj.getId(), tenantId);
		}

		delete(id, null, tenantId);
	}

	public ResponseEntity<String> deleteCabinet(String id, String version, String tenantId) {
		//String folderServiceUrl = eurekaUrlResolver.procureUrl(folderServiceId);
		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(folderServiceUrl + "folders" + "/" + id);
		// Add query parameter
		if (version != null) {
			builder.queryParam("version", version);
		}
		builder.queryParam("isCabinet", true);
		builder.queryParam("recursive", true);

		HttpHeaders headers = new HttpHeaders();
		headers.set("tenantId", tenantId);
	
		HttpEntity<String> request = new HttpEntity<>(headers);
		return restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, request, String.class);
	}

}
