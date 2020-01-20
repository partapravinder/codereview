package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.UserGroupAssociation;

public interface HybridRoleRightUserGroupAssociationDao {

	UserGroupAssociation insert(UserGroupAssociation userGroupAssociation);

	List<UserGroupAssociation> insertAll(List<UserGroupAssociation> userGroupAssociation);

	UserGroupAssociation findById(String id, String tenantId);
	
	UserGroupAssociation findByGroup(String group, String tenantId);

	UserGroupAssociation findAndRemoveById(String id, String tenantId);

	

	UserGroupAssociation findAndModify(String id, Map<String, Object> updateParams, String tenantId);

	List<UserGroupAssociation> findAll(Map<String, String[]> paramMap, String tenantId);

	UserGroupAssociation save(UserGroupAssociation userGroupAssociation);

	List<UserGroupAssociation> saveAll(List<UserGroupAssociation> userGroupAssociation);

	UserGroupAssociation findAndModifyByGroup(String groupId, Map<String, Object> updateParams, String tenantId);

}