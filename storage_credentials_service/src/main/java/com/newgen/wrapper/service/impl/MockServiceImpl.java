package com.newgen.wrapper.service.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.newgen.dto.StorageCredentialDTO;
import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "development" })
public class MockServiceImpl implements WrapperService {

	private JSONArray storageCredJsonArray = new JSONArray();

	public ResponseEntity<String> createStorageCredentials(StorageCredentialDTO storageCredentialDTO,String tenantId) throws JSONException {
		Gson gson = new Gson();
		String outputJson = gson.toJson(storageCredentialDTO);
		JSONObject jsonObj = new JSONObject(outputJson);
		jsonObj.put("id", "58f9b9b6b55e1f1c50471de7");
		jsonObj.put("creationDateTime", "1492761014811");
		jsonObj.put("version", "0");
		storageCredJsonArray.put(jsonObj);
		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> list(String tenantId) {
		return new ResponseEntity<String>(storageCredJsonArray.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> readStorageCredential(String id,String tenantId) throws JSONException {
		return new ResponseEntity<String>(storageCredJsonArray.get(0).toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> deleteStorageCredential(String id, String version,String tenantId) throws JSONException {
		storageCredJsonArray.remove(storageCredJsonArray.length());
		return new ResponseEntity<String>(storageCredJsonArray.get(0).toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> updateStorageCredential(String updateParams, String id, Long version,String tenantId) throws JSONException {
		//JSONObject jsonObj = new JSONObject(updateParams);
		/*for (Object key : jsonObj.keySet()) {
			String keyStr = (String) key;
			storageCredJsonArray.getJSONObject(0).put(keyStr, jsonObj.get(keyStr));
		}*/
		return new ResponseEntity<String>(storageCredJsonArray.get(0).toString(), HttpStatus.OK);
	}
}