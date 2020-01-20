package com.newgen.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;

public interface HybridRoleRightGroupProfileDao {

	GroupProfile insert(GroupProfile groupProfile);

	List<GroupProfile> insertAll(List<GroupProfile> groupProfile);

	GroupProfile findById(String id, String tenantId);

	GroupProfile findByGroup(String group, String tenantId);

	GroupProfile findAndRemoveById(String id, String tenantId);

	GroupProfile findAndModify(String id, Map<String, Object> updateParams, String tenantId);

	List<GroupProfile> findAll(Map<String, String[]> paramMap, String tenantId);

	GroupProfile save(GroupProfile groupProfile);

	List<GroupProfile> saveAll(List<GroupProfile> groupProfile);
	GroupProfile findAndDeallocateUserFromGroupId(String tenantId, String groupId, String[] userIds)  throws CustomException;

	// GroupProfile findAndModifyByGroup(String groupId, Map<String, Object>
	// updateParams, String tenantId);
	GroupProfile findAndModifyByGroup(String updateParams, String tenantId) throws JsonParseException, JsonMappingException, IOException;

}