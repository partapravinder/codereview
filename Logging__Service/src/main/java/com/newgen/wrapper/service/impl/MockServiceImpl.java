package com.newgen.wrapper.service.impl;

import org.json.JSONException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "development" })
public class MockServiceImpl implements WrapperService {


	@Override
	public ResponseEntity<String> deleteChildFolders(String folderId, String version, String tenantId) throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}
	
	 

}
