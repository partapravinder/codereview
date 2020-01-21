package com.newgen.wrapper.service.impl;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.newgen.dto.FolderDTO;
import com.newgen.exception.CustomException;
import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "development" })
public class MockServiceImpl implements WrapperService {
	private static final Logger logger = LoggerFactory.getLogger(MockServiceImpl.class);

	private JSONArray folderServiceJsonArray = new JSONArray();

	@Override
	public ResponseEntity<String> createFolder(FolderDTO folderDTO) throws CustomException, JSONException {

		Gson gson = new Gson();
		String outputJson = gson.toJson(folderDTO);
		JSONObject jsonObj = new JSONObject(outputJson);
		jsonObj.put("id", "58f9b9b6b55e1f1c50471de7");
		jsonObj.put("creationDateTime", "1492761014811");
		jsonObj.put("version", "0");
		JSONObject jsonObj2 = new JSONObject();
		jsonObj2.put("childrenCount", "0");
		jsonObj.put("stats", jsonObj2);
		folderServiceJsonArray.put(jsonObj);
		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> searchFolders(HttpServletRequest request) throws CustomException {
		return new ResponseEntity<String>(folderServiceJsonArray.toString(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteFolder(String id, String version, boolean recursive) throws CustomException, JSONException {
		folderServiceJsonArray.remove(folderServiceJsonArray.length());
		try {
			return new ResponseEntity<String>(folderServiceJsonArray.get(0).toString(), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<String> updateFolder(String updateFolderParams, String id, Long version)
			throws CustomException, JSONException {
		JSONObject jsonObj = new JSONObject(updateFolderParams);
		/*for (Object key : jsonObj.keySet()) {
			String keyStr = (String) key;
			folderServiceJsonArray.getJSONObject(0).put(keyStr, jsonObj.get(keyStr));
		}*/
		@SuppressWarnings("unchecked")
		Iterator<String> itr = jsonObj.keys();
		if(itr.hasNext()){
			String keyStr = (String) itr.next();
			folderServiceJsonArray.getJSONObject(0).put(keyStr, jsonObj.get(keyStr));
		}
		return new ResponseEntity<String>(folderServiceJsonArray.get(0).toString(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> moveFolder(String id, String targetFolderId, Long version) throws CustomException, JSONException {
		try {
			return new ResponseEntity<String>(folderServiceJsonArray.get(0).toString(), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<String> copyFolder(String id, String targetFolderId) throws CustomException, JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("token", "58fe26d59e489f059c024727");
		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> copyFolderStatus(String token) throws CustomException, JSONException {
		// TODO Auto-generated method stub
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", "58fe26d59e489f059c024727");
		jsonObj.put("action", "COPY");
		jsonObj.put("sourceFolderId", "58fe26ae9e489f059c024726");
		jsonObj.put("targetFolderId", "58fe26ae9e489f059c024726");
		jsonObj.put("progress", 2);
		jsonObj.put("status", "COMPLETED");
		jsonObj.put("createdDateTime", Long.valueOf("1493051093419"));
		jsonObj.put("statusChangeDateTime", Long.valueOf("1493051111647"));
		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> checkIfFolder(String id) throws CustomException {
		ResponseEntity<String> response = new ResponseEntity<>(folderServiceJsonArray.toString(), HttpStatus.OK);
		return response;
	}

	@Override
	public ResponseEntity<String> fetchParentFolderId(String id) throws CustomException {
		return new ResponseEntity<>(folderServiceJsonArray.toString(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> fetchFolderChildren(String parentFolderId) {
		return new ResponseEntity<>(folderServiceJsonArray.toString(), HttpStatus.OK);
	}

	@Override
	public void updateStats(String id) {
		logger.debug("Stats updated...");
	}

	@Override
	public ResponseEntity<String> fetchFolder(String folderId) {
		// TODO Auto-generated method stub
		return null;
	}


}
