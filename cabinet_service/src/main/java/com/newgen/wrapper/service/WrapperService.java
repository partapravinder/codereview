package com.newgen.wrapper.service;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

public interface WrapperService {

	public  ResponseEntity<String> deleteChildFolders(String id,String vesion, String tenantId) throws JSONException;
	

}
