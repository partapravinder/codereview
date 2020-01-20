package com.newgen.wrapper.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.newgen.controller.HybridRoleRightUserGroupAssociationController;
import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.dao.HybridRoleRightUserGroupAssociationDao;
import com.newgen.model.GroupProfile;
import com.newgen.model.GroupUserAssociation;
import com.newgen.model.UserGroupAssociation;
import com.newgen.model.UserProfile;
import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "production", "default" })
public class WrapperServiceImpl implements WrapperService {

	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserGroupAssociationController.class);

	@Autowired
	HybridRoleRightUserGroupAssociationDao hybridRoleRightUserGroupAssociationDao;

	@Autowired
	HybridRoleRightGroupUserAssociationDao hybridRoleRightGroupUserAssociationDao;

	@Autowired
	RestTemplate restTemplate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String readUserRightsById(String id, String userId, String tenantId, String authToken) {
		logger.debug("Entering readUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserGroupAssociationById()");

		// UserProfile userProfile =
		// restTemplate.getForObject("http://localhost:8189/userprofile?userId" + id,
		// UserProfile.class);
		// As this doesn't support setting headers, using exchange method below

		HttpHeaders headers = new HttpHeaders();
		headers.set("authToken", authToken);
		headers.set("tenantId", tenantId);
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		HttpEntity<?> entity;
		entity = new HttpEntity(headers);

		ResponseEntity<List<UserProfile>> response = (ResponseEntity<List<UserProfile>>) restTemplate.exchange(
				"http://localhost:8189/userprofile?userId=" + userId + "&profile.objectId=" + id, HttpMethod.GET,
				entity, new ArrayList<UserProfile>().getClass());

		int r1 = 0;
		com.newgen.model.Profile rightsFromUserProfile = response.getBody().get(0).getProfile().get(0);
		if (rightsFromUserProfile != null) {
			r1 = Integer.parseInt(rightsFromUserProfile.getRights(), 2);
		}

		//

		ResponseEntity<GroupUserAssociation> response1 = restTemplate.exchange(
				"http://localhost:8189/groupuserassociation/" + userId, HttpMethod.GET, entity,
				GroupUserAssociation.class);

		List<String> groupIds = new ArrayList<String>();
		groupIds = response1.getBody().getGroupIds();

		int result = 0;
		if (groupIds != null) {
			String s = "";
			int i = 0;
			for (String s1 : groupIds) {
				if (i == 0) {
					s = s + "groupId=" + s1;
				} else {
					s = s + "&groupId=" + s1;
				}
			}

			ResponseEntity<List<GroupProfile>> response2 = (ResponseEntity<List<GroupProfile>>) restTemplate.exchange(
					"http://localhost:8189/groupprofile?" + s + "&profile.objectId=" + id, HttpMethod.GET, entity,
					(new ArrayList<GroupProfile>()).getClass());

			List<GroupProfile> groups = response2.getBody();
			int prev = 0, curr;
			for (GroupProfile groupProfile : groups) {
				curr = Integer.parseInt(groupProfile.getProfile().get(0).getRights(), 2);
				result = prev & curr;
				prev = curr;
			}

		}
		String rights = Integer.toBinaryString(result & r1);

		return rights;
	}

	/*
	 * @Override public List<UserGroupAssociation> search(Map<String, String[]>
	 * allRequestParams, String tenantId) { logger.debug("Entering search()");
	 * List<UserGroupAssociation> userGroupAssociations =
	 * hybridRoleRightUserGroupAssociationDao .findAll(allRequestParams, tenantId);
	 * 
	 * userGroupAssociations.stream().forEach(userGroupAssociation -> {
	 * List<com.newgen.model.Profile> favouriteProfile =
	 * userGroupAssociation.getProfile().stream() .filter(profile ->
	 * (allRequestParams.containsKey("profile.parentFolderId") &&
	 * allRequestParams.get("profile.parentFolderId").length > 0 &&
	 * profile.getParentFolderId() != null &&
	 * allRequestParams.get("profile.parentFolderId")[0].equals(profile.
	 * getParentFolderId())) ||
	 * (!allRequestParams.containsKey("profile.parentFolderId")))
	 * .collect(Collectors.toList()); userGroupAssociation.setProfile(null);
	 * userGroupAssociation.setProfile(favouriteProfile); }); return
	 * userGroupAssociations; }
	 */
	@Override
	public UserGroupAssociation deleteUserGroupAssociation(String id, String tenantId) {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		return hybridRoleRightUserGroupAssociationDao.findAndRemoveById(id, tenantId);
	}

	@Override
	public UserGroupAssociation updateUserGroupAssociationById(Map<String, String> updateParams, String userId,
			String tenantId) {
		logger.debug("Entering updateUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit updateUserGroupAssociationById()");
		return hybridRoleRightUserGroupAssociationDao.findAndModify(userId, updateParams, tenantId);
	}

	@Override
	public UserGroupAssociation updateUserGroupAssociation(Map<String, Object> updateParams, String groupId,
			String tenantId) {
		logger.debug("Entering updateUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		UserGroupAssociation userGroupAssociationRegistration = hybridRoleRightUserGroupAssociationDao
				.findAndModifyByGroup(groupId, updateParams, tenantId);
		// (userId, updateParams,
		// tenantId);
		if (userGroupAssociationRegistration == null) {
			userGroupAssociationRegistration = hybridRoleRightUserGroupAssociationDao.findByGroup(groupId, tenantId);
		}
		logger.debug("Exit updateUserGroupAssociation()");
		return userGroupAssociationRegistration;
	}

}
