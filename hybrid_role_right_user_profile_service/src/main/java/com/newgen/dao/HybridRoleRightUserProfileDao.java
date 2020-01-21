package com.newgen.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.exception.CustomException;
import com.newgen.model.InOutParameters;
import com.newgen.model.UserProfile;

import io.swagger.models.auth.In;

public interface HybridRoleRightUserProfileDao {

	UserProfile insert(UserProfile userProfile);

	List<UserProfile> insertAll(List<UserProfile> userProfile);

	InOutParameters findById(String id, String tenantId);

	InOutParameters findFavouritesById(String id, String tenantId);

	UserProfile findByGroup(String group, String tenantId);

	InOutParameters findAndRemoveById(String id, String tenantId);

	// UserProfile findAndModify(String id, Map<String, String> updateParams, String
	// tenantId);

	InOutParameters findAndModify(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	InOutParameters findAll(Map<String, String[]> paramMap, String tenantId);

	InOutParameters findAllFavourites(Map<String, String[]> paramMap, String tenantId);

	InOutParameters save(UserProfile userProfile);

	InOutParameters saveAll(List<UserProfile> userProfile);

	InOutParameters findAndDeallocateGroupFromUserId(String tenantId, String userId, String[] groupIds)
			throws CustomException;

	UserProfile findAndModifyByGroup(String userId, Map<String, Object> updateParams, String tenantId);

	public InOutParameters findAndDeallocateProfileFromUserId(String tenantId, String userId, String[] objectIds)
			throws CustomException;

}