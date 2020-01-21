package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.InOutParameters;
import com.newgen.model.UserGroupAssociation;

public interface HybridRoleRightUserGroupAssociationDao {

	UserGroupAssociation insert(UserGroupAssociation userGroupAssociation);

	List<UserGroupAssociation> insertAll(List<UserGroupAssociation> userGroupAssociation);

	InOutParameters findById(String id, String tenantId);

	UserGroupAssociation findByGroup(String group, String tenantId);

	InOutParameters findAndRemoveById(String id, String tenantId);

	UserGroupAssociation findAndModify(String id, Map<String, Object> updateParams, String tenantId);

	InOutParameters findAll(Map<String, String[]> paramMap, String tenantId);

	InOutParameters save(UserGroupAssociation userGroupAssociation);

	List<UserGroupAssociation> saveAll(List<UserGroupAssociation> userGroupAssociation);

	UserGroupAssociation findAndModifyByGroup(String groupId, Map<String, Object> updateParams, String tenantId);

}