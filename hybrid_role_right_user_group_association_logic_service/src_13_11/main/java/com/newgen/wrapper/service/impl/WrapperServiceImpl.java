package com.newgen.wrapper.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.controller.ExceptionThrower;
import com.newgen.controller.HybridRoleRightUserGroupAssociationController;
import com.newgen.dao.ContentDao;
import com.newgen.dao.FolderDao;
import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.dao.HybridRoleRightUserGroupAssociationDao;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.Folder;
import com.newgen.model.GroupProfile;
import com.newgen.model.UserGroupAssociation;
import com.newgen.model.UserProfile;
import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "production", "default" })
public class WrapperServiceImpl implements WrapperService {

	private JSONArray arrStr;
	private String str = "";

	private String rights = "0";
	private boolean exitFromProfileCounter = false;
	private JSONArray profilesForObjectRights = null;
	private JSONArray jsonArrayFolders = null;

	private JSONArray jsonArrayCabinets = null;

	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserGroupAssociationController.class);

	@Value("${userprofile.service.url}")
	private String userprofileUrl;

	@Value("${content.service.url}")
	private String contentUrl;

	@Value("${folder.service.url}")
	private String folderUrl;

	@Value("${groupprofile.service.url}")
	private String groupprofileUrl;

	@Autowired
	FolderDao folderDao;

	@Autowired
	ContentDao contentDao;

	@Autowired
	ExceptionThrower exceptionThrower;

	@Autowired
	HybridRoleRightUserGroupAssociationDao hybridRoleRightUserGroupAssociationDao;

	@Autowired
	HybridRoleRightGroupUserAssociationDao hybridRoleRightGroupUserAssociationDao;

	@Autowired
	RestTemplate restTemplate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String readUserRightsById(String id, String userId, String tenantId, String objectType)
			throws JsonProcessingException, CustomException {
		logger.debug("Entering readUserGroupAssociationById()");
		logger.debug("Exit readUserGroupAssociationById()");

		HttpHeaders headers = new HttpHeaders();

		headers.set("tenantId", tenantId);
		headers.set("Content-Type", "application/json");

		HttpEntity<?> entity;
		entity = new HttpEntity(headers);

		String folderPrivilege = "";
		String ownerId = "";
		rights = "0";
		arrStr = null;

		if (objectType.equalsIgnoreCase("folder")) {
			Folder folder = folderDao.findById(id, tenantId);
			if (folder == null) {
				exceptionThrower.throwUserIdNotPresentInThisGroup();
			}
			folderPrivilege = folder.getPrivilege().toString();
			ownerId = folder.getOwnerId();

		} else if (objectType.equalsIgnoreCase("content")) {
			Content content = contentDao.findOne(id, tenantId);
			ownerId = content.getOwnerId();
		} else {
			exceptionThrower.throwInvalidObjectType();
		}

		if (ownerId.equalsIgnoreCase(userId)) {
			return "1111111";
		}

		ObjectMapper mapper = new ObjectMapper();
		if (folderPrivilege.toUpperCase().equals("SHARED")) {
			ResponseEntity<String> response = null;
			try {
				String url = userprofileUrl + "/userprofile?userId=" + userId + "&profiles.objectId=" + id;

				List<UserProfile> userProfiles = new ArrayList<UserProfile>();
				HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(userProfiles), headers);
				response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int r1 = 0;
			if (response != null) {
				if (response.getStatusCode().equals(HttpStatus.OK)) {
					JSONArray array1 = new JSONArray(response.getBody());
					IntStream.range(0, array1.length()).forEach(counter -> {
						if (array1.getJSONObject(counter).has("groupIds")) {
							JSONArray groupIds1 = array1.getJSONObject(counter).getJSONArray("groupIds");
							arrStr = groupIds1;
						}
					});

					String s10 = null;
					if (array1 != null && array1.length() > 0 && array1.getJSONObject(0).has("profiles")) {

						s10 = array1.getJSONObject(0).getJSONArray("profiles").getJSONObject(0).getString("rights");
					}
					if (s10 != null) {
						r1 = Integer.parseInt(s10, 2);
					}
				}
			}
			if (arrStr == null) {
				ResponseEntity<String> response01 = null;
				try {
					String url = userprofileUrl + "/userprofile/" + userId;

					List<UserProfile> userProfiles = new ArrayList<UserProfile>();
					HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(userProfiles), headers);
					response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

				} catch (RestClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (response.getStatusCode().equals(HttpStatus.FOUND)) {
					JSONObject object1 = new JSONObject(response.getBody());
					if (object1.has("groupIds")) {
						JSONArray array1 = object1.getJSONArray("groupIds");
						arrStr = array1;

					}
				}
			}
			//
			int result = 0;
			str = "";
			if (arrStr != null) {

				IntStream.range(0, arrStr.length()).forEach(counter -> {
					String s = "";
					if (counter == 0) {
						s = s + "groupId=" + arrStr.get(counter);
					} else {
						s = s + "&groupId=" + arrStr.get(counter);
					}
					counter++;
					str = str + s;
				});

				ResponseEntity<String> response2 = restTemplate.exchange(
						groupprofileUrl + "/groupprofile?" + str + "&profiles.objectId=" + id, HttpMethod.GET, entity,
						String.class);

				if (response2.getStatusCode().equals(HttpStatus.OK)) {
					JSONArray array = new JSONArray(response2.getBody());
					List<GroupProfile> groups = new ArrayList<GroupProfile>();
					IntStream.range(0, array.length()).forEach(counter -> {
						GroupProfile profile = new GroupProfile();
						profile.setGroupId(array.getJSONObject(counter).get("groupId").toString());
						if (array.getJSONObject(counter).has("rights")) {
							profile.setRights(array.getJSONObject(counter).get("rights").toString());
							groups.add(profile);
						}
					});

					int prev = 0, curr;
					for (GroupProfile groupProfile : groups) {
						curr = Integer.parseInt(groupProfile.getRights(), 2);
						result = prev | curr;
						prev = curr;
					}

				}
			}
			rights = Integer.toBinaryString(result | r1);

		} else if (folderPrivilege.toUpperCase().equals("PRIVATE")) {

			if (ownerId.equals(userId)) {
				return "1111111";
			} else {
				return "0000000";
				// throw exception
			}

			// } else if (folderPrivilege.toUpperCase().equals("INHERITED")) {
		} else {
			ResponseEntity<String> response1 = null;
			List<String> objectIds = new ArrayList<String>();
			String url1 = "";
			if (objectType.equalsIgnoreCase("folder")) {
				url1 = folderUrl + "/folders/rootpathhierarchy/" + id;
			} else if (objectType.equalsIgnoreCase("content")) {
				url1 = contentUrl + "/contents/rootpathhierarchy/" + id;
			}
			System.out.println("url1---->>" + url1);
			response1 = restTemplate.exchange(url1, HttpMethod.GET, entity, String.class);
			JSONObject json = null;
			System.out.println(response1);
			System.out.println(response1.getBody());
			System.out.println(response1.getStatusCode());
			System.out.println(response1.getStatusCodeValue());
			if (response1 != null && response1.getStatusCode().equals(HttpStatus.OK)) {
				json = new JSONObject(response1.getBody());

				System.out.println("json---->>>" + json);
				// JSONArray array = json.getJSONArray("folder");

				System.out.println("json.optJSONArray(folder)==>" + json.optJSONArray("folder"));
				System.out.println("json.optJSONObject(folder)==>" + json.optJSONObject("folder"));
				System.out.println("json.opt(folder)==>" + json.opt("folder"));
				if (json.optJSONArray("folder") != null) {
					jsonArrayFolders = json.getJSONArray("folder");
				} else {
					jsonArrayFolders = new JSONArray();
					if (json.optJSONObject("folder") != null) {
						jsonArrayFolders.put(json.getJSONObject("folder"));
					}
				}
				System.out.println(jsonArrayFolders.length());

				str = "";
				IntStream.range(0, jsonArrayFolders.length()).forEach(counter -> {
					String s = "";
					s = "&profiles.objectId=" + jsonArrayFolders.getJSONObject(counter).getString("id");

					// counter++;
					str = str + s;
				});

				System.out.println("optJSONObject(cabinet)==>" + json.optJSONObject("cabinet"));
				System.out.println("json.opt(cabinet)==>" + json.opt("cabinet"));

				if (json.optJSONObject("cabinet") != null) {
					str = str + "&profiles.objectId=" + json.getJSONObject("cabinet").getString("id");
				}
				System.out.println("str--->>>" + str);

				if (!str.equals("")) {
					ResponseEntity<String> response = null;
					try {
						String url = userprofileUrl + "/userprofile?userId=" + userId + str;
						System.out.println("URL----->>>" + url);

						List<UserProfile> userProfiles = new ArrayList<UserProfile>();

						HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(userProfiles), headers);
						response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
						System.out.println("response---->>>" + response);

					} catch (RestClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("------Exception Occurred------");
					}
					if (response.getStatusCode().equals(HttpStatus.OK)) {

						JSONArray array1 = new JSONArray(response.getBody());

						if (!array1.isEmpty()) {
							profilesForObjectRights = array1.getJSONObject(0).getJSONArray("profiles");
							IntStream.range(0, jsonArrayFolders.length()).forEach(counter -> {

								String objId = jsonArrayFolders.getJSONObject(counter).getString("id");
								exitFromProfileCounter = false;
								IntStream.range(0, profilesForObjectRights.length()).forEach(profileCounter -> {
									if (!exitFromProfileCounter
											&& profilesForObjectRights.getJSONObject(profileCounter).has("rights")
											&& profilesForObjectRights.getJSONObject(profileCounter)
													.getString("objectId").equals(objId)) {
										rights = profilesForObjectRights.getJSONObject(profileCounter)
												.getString("rights");
										exitFromProfileCounter = true;
									}
								});

							});
						}

					} else {
						System.out.println(
								"Response status code is not 200. Response code ->" + response.getStatusCode());
					}
				}

			} else {
				exceptionThrower.throwInvalidObjectType();
			}
		}

		if (rights.contentEquals("0")) {
			rights = "0000000";
		}

		return rights;
	}

	@Override
	public UserGroupAssociation deleteUserGroupAssociation(String id, String tenantId) {
		logger.debug("Entering deleteUserGroupAssociation()");
		logger.debug("Exit deleteKMSCredential()");
		return hybridRoleRightUserGroupAssociationDao.findAndRemoveById(id, tenantId);
	}

	@Override
	public UserGroupAssociation updateUserGroupAssociationById(Map<String, String> updateParams, String userId,
			String tenantId) {
		logger.debug("Entering updateUserGroupAssociationById()");
		logger.debug("Exit updateUserGroupAssociationById()");
		return hybridRoleRightUserGroupAssociationDao.findAndModify(userId, updateParams, tenantId);
	}

	@Override
	public UserGroupAssociation updateUserGroupAssociation(Map<String, Object> updateParams, String groupId,
			String tenantId) {
		logger.debug("Entering updateUserGroupAssociation()");
		UserGroupAssociation userGroupAssociationRegistration = hybridRoleRightUserGroupAssociationDao
				.findAndModifyByGroup(groupId, updateParams, tenantId);
		if (userGroupAssociationRegistration == null) {
			userGroupAssociationRegistration = hybridRoleRightUserGroupAssociationDao.findByGroup(groupId, tenantId);
		}
		logger.debug("Exit updateUserGroupAssociation()");
		return userGroupAssociationRegistration;
	}

}
