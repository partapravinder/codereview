package com.newgen.wrapper.service;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import com.newgen.dto.FolderDTO;
import com.newgen.exception.CustomException;

public interface WrapperService {
	public ResponseEntity<String> createFolder(FolderDTO folderDTO) throws CustomException, JSONException;

	public ResponseEntity<String> searchFolders(HttpServletRequest request) throws CustomException;

	public ResponseEntity<String> deleteFolder(String id, String version, boolean recursive) throws CustomException, JSONException;

	public ResponseEntity<String> updateFolder(String updateFolderParams, String id, Long version)
			throws CustomException, JSONException;

	public ResponseEntity<String> moveFolder(String id, String targetFolderId, Long version) throws CustomException, JSONException;

	public ResponseEntity<String> copyFolder(String id, String targetFolderId) throws CustomException, JSONException;

	public ResponseEntity<String> copyFolderStatus(String token) throws CustomException, JSONException;

	public ResponseEntity<String> checkIfFolder(String id) throws CustomException;

	public ResponseEntity<String> fetchParentFolderId(String id) throws CustomException;

	public ResponseEntity<String> fetchFolderChildren(String parentFolderId);
	
	public ResponseEntity<String> fetchFolder(String folderId);

	public void updateStats(String id);

}
