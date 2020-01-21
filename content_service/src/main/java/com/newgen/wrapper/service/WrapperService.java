package com.newgen.wrapper.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newgen.enumdef.Privilege;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.Folder;
import com.newgen.model.Lock;

public interface WrapperService {

	public ResponseEntity<String> uploadStoreContent(MultipartFile file, String name, String contentType,
			String comments, String parentFolderId, String ownerName, String ownerId, String storageCredentialId,
			String uploadPath, Boolean Async, String tenantId)
			throws CustomException, JsonProcessingException, InterruptedException, JSONException;

	/*
	 * public Content uploadMetaContent(MultipartFile file, String name, String
	 * contentType, String comments, String parentFolderId, String ownerName, String
	 * ownerId, String storageCredentialId, String token, String noOfPages, String
	 * documentSize, String documentType, String metadata,Privilege privilege,
	 * Boolean async, String locationId, String tenantId, boolean initialVersion)
	 * throws CustomException, JsonProcessingException, InterruptedException,
	 * JSONException;
	 */

	/*
	 * public Content checkInMetaContent(MultipartFile file, String name, String
	 * contentType, String comments, String parentFolderId, String ownerName, String
	 * ownerId, String storageCredentialId, String token, String noOfPages, String
	 * documentSize, String documentType, String metadata,Privilege privilege,
	 * Boolean async, String locationId, String tenantId, boolean initialVersion,
	 * String primaryDocumentId, Date creationDate, BigDecimal previousVersion,
	 * BigDecimal version, String checkedInBy) throws CustomException,
	 * JsonProcessingException, InterruptedException, JSONException;
	 */
	
	public Content uploadMetaContent(MultipartFile file, String name, String contentType, String comments,
			String parentFolderId, String ownerName, String ownerId, String storageCredentialId, String token,
			String noOfPages, String documentSize, String documentType, String metadata,Privilege privilege, Boolean async,
			String locationId, String tenantId, boolean initialVersion, String dataclass)
					throws CustomException, JsonProcessingException, InterruptedException, JSONException;
	
	public Content checkInMetaContent(MultipartFile file, String name, String contentType, String comments,
			String parentFolderId, String ownerName, String ownerId, String storageCredentialId, String token,
			String noOfPages, String documentSize, String documentType, String metadata,Privilege privilege, Boolean async,
			String locationId, String tenantId, boolean initialVersion, String primaryDocumentId, Date creationDate,
			BigDecimal previousVersion, BigDecimal version, String checkedInBy, String dataclass)
					throws CustomException, JsonProcessingException, InterruptedException, JSONException;
	
	public Folder findById(String id, String tenantId) throws CustomException;

	public void acknowledgeStorageService(String token, String tenantId);

	public void deleteContent(String id, BigDecimal version, String tenantId) throws CustomException;

	public List<Content> searchContents(HttpServletRequest request, String tenantId);

	public List<Content> searchContentsByPage(HttpServletRequest request, int pno, String tenantId);

	public Content updateContentInfo(String updateContentParams, String id, BigDecimal version, String tenantId)
			throws CustomException;

	public Content moveContent(String id, String targetFolderId, BigDecimal version, String tenantId)
			throws CustomException;

	public ResponseEntity<String> retrieveContent(String id, String tenantId) throws CustomException, JSONException;

	public ResponseEntity<String> getContentUploadStatus(String token, String tenantId) throws JSONException;

	public Content fetchContentModel(String id, String tenantId) throws CustomException;

	public Content fetchLatestContentModel(String id, String tenantId) throws CustomException;

	public JSONObject getFolderHierarchy(String par_folder, String tenantId) throws CustomException;

	public boolean checkStorageLimits(String size, String tenantId) throws CustomException;

	public Lock lockContent(Lock lock);

	public Content checkOutContent(String id, String tenantId, String userId) throws CustomException;

	public Content undoCheckOutContent(String id, String tenantId, String userId) throws CustomException;

	public Content setContentVersionLatest(String id, BigDecimal version, String tenantId)
			throws CustomException;

	public Content undoCheckOutAfterCheckIn(Content latestContent, String tenantId, String userId)
			throws CustomException;

//	public ResponseEntity<String> getFolderHierarchy(String par_folder, String tenantId);
//
//	public ResponseEntity<String> searchContentMetadata(String searchParams, String tenantId);
//
//	public ResponseEntity<String> deleteContentMetadata(String contentId, String tenantId);

}
