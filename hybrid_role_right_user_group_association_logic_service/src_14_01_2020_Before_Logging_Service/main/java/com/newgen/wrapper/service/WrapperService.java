package com.newgen.wrapper.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newgen.exception.CustomException;
import com.newgen.model.UserGroupAssociation;

public interface WrapperService {

	public String readUserRightsById(String id, String userId, String tenantId, String objectType) throws JSONException, JsonProcessingException, CustomException;

	//public List<UserGroupAssociation> search(Map<String, String[]> updateParams, String tenantId) throws JSONException;

	public UserGroupAssociation deleteUserGroupAssociation(String id, String tenantId) throws JSONException;

	public UserGroupAssociation updateUserGroupAssociationById(Map<String, String> updateParams, String id, String tenantId);
	
	public UserGroupAssociation updateUserGroupAssociation(Map<String, Object> updateParams, String userId, String tenantId);
}
