package com.newgen.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.InOutParameters;

public interface HybridRoleRightGroupProfileDao {

	GroupProfile insert(GroupProfile groupProfile);

	List<GroupProfile> insertAll(List<GroupProfile> groupProfile);

	InOutParameters findById(String id, String tenantId);

	InOutParameters findByGroup(String group, String tenantId);

	InOutParameters findAndRemoveById(String id, String tenantId);

	InOutParameters findAndModify(String id, Map<String, Object> updateParams, String tenantId);

	InOutParameters findAll(Map<String, String[]> paramMap, String tenantId);

	InOutParameters save(GroupProfile groupProfile);

	InOutParameters saveAll(List<GroupProfile> groupProfile);

	InOutParameters findAndDeallocateProfileFromGroupId(String tenantId, String groupId, String[] objectIds)
			throws CustomException;

	InOutParameters findAndDeallocateUserFromGroupId(String tenantId, String groupId, String[] userIds)
			throws CustomException;

	// GroupProfile findAndModifyByGroup(String groupId, Map<String, Object>
	// updateParams, String tenantId);
	InOutParameters findAndModifyByGroup(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

}