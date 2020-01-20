package com.newgen.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.UserProfile;

public interface HybridRoleRightUserProfileDao {

	UserProfile insert(UserProfile userProfile);

	List<UserProfile> insertAll(List<UserProfile> userProfile);

	UserProfile findById(String id, String tenantId);

	UserProfile findFavouritesById(String id, String tenantId);

	UserProfile findByGroup(String group, String tenantId);

	UserProfile findAndRemoveById(String id, String tenantId);

	// UserProfile findAndModify(String id, Map<String, String> updateParams, String
	// tenantId);

	UserProfile findAndModify(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	List<UserProfile> findAll(Map<String, String[]> paramMap, String tenantId);

	List<UserProfile> findAllFavourites(Map<String, String[]> paramMap, String tenantId);

	UserProfile save(UserProfile userProfile);

	List<UserProfile> saveAll(List<UserProfile> userProfile);
	
	UserProfile findAndDeallocateGroupFromUserId(String tenantId, String userId, String[] groupIds)  throws CustomException;

	UserProfile findAndModifyByGroup(String userId, Map<String, Object> updateParams, String tenantId);

}