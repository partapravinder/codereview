//package com.newgen.wrapper.service.impl;
//
//import java.util.Iterator;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
// 
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.newgen.exception.CustomException;
//import com.newgen.wrapper.service.WrapperService;
//
//public class MockServiceImpl implements WrapperService {
//
//	private JSONArray contentJsonArray = new JSONArray();
//
//	@Override
//	public ResponseEntity<String> uploadStoreContent(MultipartFile file, String name, String contentType,
//			String comments, String parentFolderId, String ownerName, String ownerId, String storageCredentialId,
//			String uploadPath,Boolean Async,String tenantId) throws CustomException, JsonProcessingException, InterruptedException, JSONException {
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("id", "58ff1edf9e489f059c02472f");
//		jsonObj.put("status", "PENDING");
//
//		JSONObject jsonObj2 = new JSONObject();
//		jsonObj2.put("id", "58ff1edf9e489f059c02472f");
//		jsonObj2.put("flag", "COMMITTED");
//		jsonObj2.put("comments", "test comments");
//		jsonObj2.put("parentFolderId", "58ff16279e489f059c02472b");
//		jsonObj2.put("ownerId", "454564646dg");
//		jsonObj2.put("accessDateTime", Long.valueOf("1493114724431"));
//		jsonObj2.put("revisedDateTime", Long.valueOf("1493114724431"));
//		jsonObj2.put("creationDateTime", Long.valueOf("1493114702476"));
//		jsonObj2.put("version", "1");
//		jsonObj2.put("ownerName", "manoj");
//		jsonObj2.put("name", "testLibUpload");
//		jsonObj2.put("contentType", "pdf");
//		jsonObj2.put("contentLocationId", "58ff1f4e9e489f059c024730");
//		contentJsonArray.put(jsonObj2);
//
//		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
//	}
//	
//	
//
//	@Override
//	public Content uploadMetaContent(MultipartFile file, String name, String contentType,
//			String comments, String parentFolderId, String ownerName, String ownerId, String storageCredentialId,
//			String token, String noOfPages, String documentSize, String documentType, Map<String, String> metadata, String metadataId,
//			Boolean async,String locationId_temp,String tenantId) throws CustomException, JsonProcessingException, InterruptedException, JSONException {
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("id", "58ff1f4e9e489f059c024731");
//		jsonObj.put("status", "PENDING");
//
//		JSONObject jsonObj2 = new JSONObject();
//		jsonObj2.put("id", "58ff1f4e9e489f059c024731");
//		jsonObj2.put("flag", "COMMITTED");
//		jsonObj2.put("comments", "test comments");
//		jsonObj2.put("parentFolderId", "58ff16279e489f059c02472b");
//		jsonObj2.put("ownerId", "454564646dg");
//		jsonObj2.put("accessDateTime", Long.valueOf("1493114724431"));
//		jsonObj2.put("revisedDateTime", Long.valueOf("1493114724431"));
//		jsonObj2.put("creationDateTime", Long.valueOf("1493114702476"));
//		jsonObj2.put("version", "1");
//		jsonObj2.put("ownerName", "manoj");
//		jsonObj2.put("name", "testLibUpload");
//		jsonObj2.put("contentType", "pdf");
//		jsonObj2.put("contentLocationId", "58ff1f4e9e489f059c024730");
//		jsonObj2.put("metadataId", "2325");
//		jsonObj2.put("metadata", "{\"k1\":\"value1\",\"k2\":\"value2\"}");
//
//		contentJsonArray.put(jsonObj2);
//		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> deleteContent(String id, String version,String tenantId) {
//		contentJsonArray.remove(contentJsonArray.length());
//		return null;
//	}
//
//	
//	@Override
//	public void acknowledgeStorageService(String token) {
//	 
//	}
//
// 
//
//
//	@Override
//	public ResponseEntity<String> searchContents(HttpServletRequest request,String tenantId) {
//		return new ResponseEntity<String>(contentJsonArray.toString(), HttpStatus.OK);
//	}
//	
//	@Override
//	public ResponseEntity<String> searchContentsByPage(HttpServletRequest request,int pno,String tenantId) {
//		return new ResponseEntity<String>(contentJsonArray.toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> updateContentInfo(String updateContentParams, String id, Long version,String tenantId) throws JSONException {
//		JSONObject jsonObj = new JSONObject(updateContentParams);
//		/*for (Object key : jsonObj.keySet()) {
//			String keyStr = (String) key;
//			contentJsonArray.getJSONObject(0).put(keyStr, jsonObj.get(keyStr));
//		}*/
//		@SuppressWarnings("unchecked")
//		Iterator<String> itr = jsonObj.keys();
//		if(itr.hasNext()){
//			String keyStr = (String) itr.next();
//			contentJsonArray.getJSONObject(0).put(keyStr, jsonObj.get(keyStr));
//		}
//		return new ResponseEntity<String>(contentJsonArray.get(0).toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> copyContent(String parentFolderId, String id,String tenantId) throws JSONException {
//		return new ResponseEntity<String>(contentJsonArray.get(0).toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> moveContent(String id, String targetFolderId, Long version,String tenantId) throws JSONException {
//		return new ResponseEntity<String>(contentJsonArray.get(0).toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> contentStatus(String id,String tenantId) throws JSONException {
//		return new ResponseEntity<String>(contentJsonArray.get(0).toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> retrieveContent(String id,String tenantId) throws CustomException, JSONException {
//		return new ResponseEntity<String>(contentJsonArray.get(0).toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> getContentUploadStatus(String token,String tenantId) throws JSONException {
//		return new ResponseEntity<String>(contentJsonArray.get(0).toString(), HttpStatus.OK);
//	}
//
//	@Override
//	public ResponseEntity<String> fetchContentModel(String id,String tenantId) throws JSONException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
