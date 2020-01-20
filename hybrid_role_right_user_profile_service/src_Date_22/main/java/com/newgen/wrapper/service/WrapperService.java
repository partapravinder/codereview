package com.newgen.wrapper.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.dto.GroupProfileDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.GroupProfile;
import com.newgen.model.GroupUserAssociation;
import com.newgen.model.UserGroupAssociation;
import com.newgen.model.UserProfile;

public interface WrapperService {

	public UserProfile registerUser(UserProfile userProfileRegistration) throws JSONException, CustomException;

	public List<UserProfile> registerBulkUsers(List<UserProfile> userProfileRegistration, String tennatId)
			throws JSONException, CustomException;

	public List<UserProfile> listUsers(String tenantId);

	public UserProfile readUserProfileById(String id, String tenantId) throws JSONException;

	public UserProfile readUserProfileFavouritesById(String id, String tenantId) throws JSONException;

	public List<UserProfile> searchUsers(Map<String, String[]> updateParams, String tenantId) throws JSONException;

	public List<UserProfile> searchFavourites(Map<String, String[]> updateParams, String tenantId) throws JSONException;

	public UserProfile deleteUserProfile(String id, String tenantId) throws JSONException;

	// public UserProfile updateUserProfileById(Map<String, String> updateParams,
	// String id, String tenantId);

	// public UserProfile updateUserProfile(Map<String, Object> updateParams, String
	// userId, String tenantId,
	// UserProfileDTO userProfileDTO);

	public UserProfile updateUserProfile(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public GroupProfile registerGroup(GroupProfile groupProfileRegistration)
			throws JSONException, IOException, CustomException;

	public List<GroupProfile> registerBulkGroups(List<GroupProfile> groupProfileRegistration, String tenantId)
			throws JSONException;

	public List<GroupProfile> listGroups(String tenantId);

	public GroupProfile readGroupProfileById(String id, String tenantId) throws JSONException;

	public List<GroupProfile> searchGroups(Map<String, String[]> updateParams, String tenantId) throws JSONException;

	public GroupProfile deleteGroupProfile(String id, String tenantId) throws JSONException;

	public GroupProfile updateGroupProfileById(Map<String, String> updateParams, String id, String tenantId);

	// public GroupProfile updateGroupProfile(Map<String, Object> updateParams,
	// String groupId, String tenantId);
	public GroupProfile updateGroupProfile(GroupProfileDTO groupProfileDTO, String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException, CustomException;

	public UserGroupAssociation register(UserGroupAssociation userGroupAssociationRegistration) throws JSONException;

	public List<UserGroupAssociation> registerBulk(List<UserGroupAssociation> userGroupAssociationRegistration)
			throws JSONException;

	public List<UserGroupAssociation> list(String tenantId);

	public UserGroupAssociation readUserGroupAssociationById(String id, String tenantId) throws JSONException;

	public GroupUserAssociation readGroupUserAssociationById(String id, String tenantId) throws JSONException;

	// public List<UserGroupAssociation> search(Map<String, String[]> updateParams,
	// String tenantId) throws JSONException;

	public UserGroupAssociation deleteUserGroupAssociation(String id, String tenantId) throws JSONException;

	public GroupProfile deallocateUserFromGroup(String tenantId, String groupId, String[] userIds)
			throws JSONException, CustomException;

	public UserProfile deallocateGroupFromUser(String tenantId, String userId, String[] groupIds)
			throws JSONException, CustomException;

	public UserGroupAssociation updateUserGroupAssociationById(Map<String, Object> updateParams, String id,
			String tenantId);

	public GroupProfile updateGroupProfile(String updateParams, String groupId, String tenantId)
			throws JsonParseException, JsonMappingException, IOException;

	public GroupProfile deallocateProfileFromGroup(String tenantId, String groupId, String[] objectIds)
			throws JSONException, CustomException;

	public UserProfile deallocateProfileFromUser(String tenantId, String userId, String[] objectIds)
			throws JSONException, CustomException;

}
