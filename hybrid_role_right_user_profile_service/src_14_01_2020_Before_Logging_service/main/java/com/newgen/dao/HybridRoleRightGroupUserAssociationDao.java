package com.newgen.dao;

import java.util.List;
import java.util.Map;

import com.newgen.model.GroupUserAssociation;

public interface HybridRoleRightGroupUserAssociationDao {

	GroupUserAssociation insert(GroupUserAssociation userGroupAssociation);

	List<GroupUserAssociation> insertAll(List<GroupUserAssociation> userGroupAssociation);

	GroupUserAssociation findById(String id, String tenantId);

	GroupUserAssociation findByGroup(String group, String tenantId);

	GroupUserAssociation findAndRemoveById(String id, String tenantId);

	GroupUserAssociation findAndModify(String id, Map<String, String> updateParams, String tenantId);

	List<GroupUserAssociation> findAll(Map<String, String[]> paramMap, String tenantId);

	GroupUserAssociation save(GroupUserAssociation userGroupAssociation);

	List<GroupUserAssociation> saveAll(List<GroupUserAssociation> userGroupAssociation);

	GroupUserAssociation findAndModifyByGroup(String userId, GroupUserAssociation groupUserAssociation, String tenantId);

}
