package com.newgen.wrapper.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.newgen.controller.ExceptionThrower;
import com.newgen.controller.HybridRoleRightUserGroupProfileController;
import com.newgen.dao.ContentDao;
import com.newgen.dao.FolderDao;
import com.newgen.dao.HybridRoleRightGroupProfileDao;
import com.newgen.dao.HybridRoleRightGroupUserAssociationDao;
import com.newgen.dao.HybridRoleRightUserGroupAssociationDao;
import com.newgen.dao.HybridRoleRightUserProfileDao;
import com.newgen.dto.GroupProfileDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.Folder;
import com.newgen.model.GroupProfile;
import com.newgen.model.GroupUserAssociation;
import com.newgen.model.UserGroupAssociation;
import com.newgen.model.UserProfile;
import com.newgen.model.ValidationError;
//import com.newgen.service.EurekaUrlResolver;
import com.newgen.validation.ValidationErrorBuilder;
import com.newgen.wrapper.service.WrapperService;

@Component
@Profile({ "production", "default" })
public class WrapperServiceImpl implements WrapperService {

	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserGroupProfileController.class);

	@Autowired
	ExceptionThrower exceptionThrower;

	@Autowired
	FolderDao folderDao;

	@Autowired
	ContentDao contentDao;

	@Autowired
	HybridRoleRightUserProfileDao hybridRoleRightUserProfileDao;

	@Autowired
	HybridRoleRightGroupProfileDao hybridRoleRightGroupProfileDao;

	@Autowired
	HybridRoleRightUserGroupAssociationDao hybridRoleRightUserGroupAssociationDao;

	@Autowired
	HybridRoleRightGroupUserAssociationDao hybridRoleRightGroupUserAssociationDao;

	boolean validRequest = true;

	@Override
	public UserProfile registerUser(UserProfile userProfile) throws CustomException {
		logger.debug("Entering register()");
		System.out.println(userProfile.getTenantId());
		// Create Secured Data object

		String tenantId = userProfile.getTenantId();
		String userId = userProfile.getUserId();

		validRequest = true;
		if (userProfile.getProfiles() != null) {
			userProfile.getProfiles().stream().forEach(profile -> {
				if (profile.getObjectType().equalsIgnoreCase("folder")) {
					Folder folder = folderDao.findById(profile.getObjectId(), tenantId);
					System.out.println(folder);
					if (folder != null && folder.getParentFolderId() != null) {
						profile.setParentFolderId(folder.getParentFolderId());
					} else {
						validRequest = false;
					}
				} else if (profile.getObjectType().equalsIgnoreCase("content")) {
					Content content = contentDao.findOne(profile.getObjectId(), tenantId);
					if (content != null && content.getParentFolderId() != null) {
						profile.setParentFolderId(content.getParentFolderId());
					} else {
						validRequest = false;
					}
				}
			});
		}

		if (!validRequest) {
			exceptionThrower.throwInvalidObjectType();
		}

		userProfile = hybridRoleRightUserProfileDao.save(userProfile);

		if (userProfile.getGroupIds() != null) {
			List<GroupProfile> groupProfiles = new ArrayList<GroupProfile>();
			userProfile.getGroupIds().stream().forEach(g -> {

				GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findById(g, tenantId);
				Set<String> groupProfileUserList = new HashSet<String>();
				if (groupProfile != null) {
					if (groupProfile.getUserIds() != null) {
						groupProfileUserList.addAll(groupProfile.getUserIds());
					}
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					JSONObject updateParams = new JSONObject(groupProfile);
					System.out.println("updateParams--->" + updateParams);
					hybridRoleRightGroupProfileDao.findAndModify(g, updateParams.toMap(), tenantId);

				} else {
					groupProfile = new GroupProfile();
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					groupProfile.setTenantId(tenantId);
					groupProfile.setGroupId(g);
					groupProfiles.add(groupProfile);
				}
			});
			if (groupProfiles.size() > 0)
				hybridRoleRightGroupProfileDao.saveAll(groupProfiles);
		}
		logger.debug("Exit register()");
		return userProfile;
	}

	@Override
	public List<UserProfile> registerBulkUsers(List<UserProfile> userProfile, String tenantId)
			throws JSONException, CustomException {
		logger.debug("Entering registerBulk()");
		// System.out.println(userProfile.getTenantId());
		// Create Secured Data object

		// userProfile = hybridRoleRightUserProfileDao.saveAll(userProfile);

		List<UserProfile> response = new ArrayList<UserProfile>();
		userProfile.stream().forEach(user -> {
			if (user.getUserId() != null) {
				UserProfile userProfile1 = readUserProfileById(user.getUserId(), tenantId);
				if (userProfile1 != null) {
					JSONObject json = new JSONObject(user);
					try {
						response.add(updateUserProfile(json.toString(), tenantId));
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					UserProfile userProfile2 = new UserProfile(tenantId, user.getUserId(), user.getProfiles());

					if (user.getGroupIds() != null) {
						userProfile2.setGroupIds(user.getGroupIds());
					}
					try {
						response.add(registerUser(userProfile2));
					} catch (CustomException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				/*
				 * UserProfile userProfile3 = new UserProfile(tenantId, user.getUserId(),
				 * user.getProfile());
				 * 
				 * if (user.getGroupIds() != null) {
				 * userProfile3.setGroupIds(user.getGroupIds()); }
				 * response.add(registerUser(userProfile3));
				 */
				try {
					exceptionThrower.throwNoUserIdNotPresentInRequest();
				} catch (CustomException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		logger.debug("Exit registerBulk()");
		return response;
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidationError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidationError createValidationError(MethodArgumentNotValidException exception) {
		return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}

	@Override
	public List<UserProfile> listUsers(String tenantId) {
		logger.debug("Entering list()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		List<UserProfile> userProfileList = hybridRoleRightUserProfileDao.findAll(null, tenantId);
		logger.debug("Exit list()");
		return userProfileList;
	}

	@Override
	public UserProfile readUserProfileById(String id, String tenantId) {
		logger.debug("Entering readUserProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserProfileById()");
		return hybridRoleRightUserProfileDao.findById(id, tenantId);
	}

	@Override
	public UserProfile readUserProfileFavouritesById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readUserProfileFavouritesById()");
		UserProfile userProfile = hybridRoleRightUserProfileDao.findFavouritesById(id, tenantId);
		List<com.newgen.model.Profile> favouriteProfile = userProfile.getProfiles().stream()
				.filter(profile -> profile.isFavourite() == true).collect(Collectors.toList());
		userProfile.setProfiles(favouriteProfile);
		return userProfile;
	}

	@Override
	public List<UserProfile> searchUsers(Map<String, String[]> allRequestParams, String tenantId) {
		logger.debug("Entering search()");
		List<UserProfile> userProfiles = hybridRoleRightUserProfileDao.findAll(allRequestParams, tenantId);

		List<UserProfile> filteredUserProfiles = new ArrayList<UserProfile>();

		JSONObject json = new JSONObject(allRequestParams);
		if (json.has("profiles.parentFolderId")) {
			userProfiles.stream().forEach(userProfile -> {
				List<com.newgen.model.Profile> favouriteProfile = userProfile.getProfiles().stream()
						.filter(profile -> (allRequestParams.containsKey("profiles.parentFolderId")
								&& allRequestParams.get("profiles.parentFolderId").length > 0
								&& profile.getParentFolderId() != null
								&& allRequestParams.get("profiles.parentFolderId")[0]
										.equals(profile.getParentFolderId()))
								|| (!allRequestParams.containsKey("profiles.parentFolderId")))
						.collect(Collectors.toList());
				userProfile.setProfiles(favouriteProfile);
				filteredUserProfiles.add(userProfile);
			});
		}

		if (json.has("profiles.objectId")) {

			userProfiles.stream().forEach(userProfile -> {

				List<com.newgen.model.Profile> favouriteProfile = userProfile.getProfiles().stream()
						.filter(profile -> (allRequestParams.containsKey("profiles.objectId")
								&& allRequestParams.get("profiles.objectId").length > 0 && profile.getObjectId() != null
				// &&
				// allRequestParams.get("profiles.objectId")[0].equals(profile.getObjectId()))
								&& Arrays.asList(allRequestParams.get("profiles.objectId"))
										.contains(profile.getObjectId()))
								|| (!allRequestParams.containsKey("profiles.objectId")))
						.collect(Collectors.toList());
				userProfile.setProfiles(favouriteProfile);
				filteredUserProfiles.add(userProfile);

			});
		}
		return filteredUserProfiles;
	}

	@Override
	public List<UserProfile> searchFavourites(Map<String, String[]> allRequestParams, String tenantId) {
		logger.debug("Entering search()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		List<UserProfile> userProfiles = hybridRoleRightUserProfileDao.findAllFavourites(allRequestParams, tenantId);

		userProfiles.stream().forEach(userProfile -> {
			List<com.newgen.model.Profile> favouriteProfile = userProfile.getProfiles().stream()
					.filter(profile -> (profile.isFavourite() == true
							&& allRequestParams.containsKey("profiles.parentFolderId")
							&& allRequestParams.get("profiles.parentFolderId").length > 0
							&& profile.getParentFolderId() != null
							&& allRequestParams.get("profiles.parentFolderId")[0].equals(profile.getParentFolderId()))
							|| (profile.isFavourite() == true
									&& !allRequestParams.containsKey("profiles.parentFolderId")))
					.collect(Collectors.toList());
			userProfile.setProfiles(null);
			userProfile.setProfiles(favouriteProfile);
		});
		return userProfiles;
	}

	@Override
	public UserProfile deleteUserProfile(String id, String tenantId) {
		logger.debug("Entering deleteUserProfile()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		UserProfile userProfile = hybridRoleRightUserProfileDao.findAndRemoveById(id, tenantId);

		if (userProfile != null) {
			if (userProfile.getGroupIds() != null) {
				userProfile.getGroupIds().stream().forEach(g -> {

					GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findById(g, tenantId);
					Set<String> groupProfileUserList = new HashSet<String>();
					if (groupProfile != null) {
						if (groupProfile.getUserIds() != null) {
							groupProfileUserList.addAll(groupProfile.getUserIds());
						}
						groupProfileUserList.remove(userProfile.getUserId());
						groupProfile.setUserIds((new ArrayList<String>(groupProfileUserList)));
						JSONObject updatedParams = new JSONObject(groupProfile);
						System.out.println("updateParams--->" + updatedParams);
						hybridRoleRightGroupProfileDao.findAndModify(g, updatedParams.toMap(), tenantId);

					}
				});
			}
		}
		return userProfile;
	}

	/*
	 * @Override public UserProfile updateUserProfileById(Map<String, String>
	 * updateParams, String userId, String tenantId) {
	 * logger.debug("Entering updateUserProfileById()"); // String url =
	 * eurekaUrlResolver.procureUrl(kmsServiceId);
	 * logger.debug("Exit updateUserProfileById()"); return
	 * hybridRoleRightUserProfileDao.findAndModify(userId, updateParams, tenantId);
	 * }
	 */

	/*
	 * @Override public UserProfile updateUserProfile(String updateParams, String
	 * tenantId) { logger.debug("Entering updateUserProfile()");
	 * System.out.println(updateParams); System.out.println("11111111111111111111");
	 * // String url = eurekaUrlResolver.procureUrl(kmsServiceId);
	 * 
	 * UserProfile userProfileRegistration =
	 * hybridRoleRightUserProfileDao.findById(userId, tenantId); if
	 * (userProfileRegistration == null) { UserProfile userProfile = new
	 * UserProfile(tenantId, userId, userProfileDTO.getProfile());
	 * userProfileRegistration = hybridRoleRightUserProfileDao.save(userProfile); }
	 * else { userProfileRegistration =
	 * hybridRoleRightUserProfileDao.findAndModifyByGroup(userId, updateParams,
	 * tenantId); } // (userId, updateParams, // tenantId); if
	 * (userProfileRegistration == null) { userProfileRegistration =
	 * hybridRoleRightUserProfileDao.findByGroup(userId, tenantId); }
	 * logger.debug("Exit updateUserProfile()"); return userProfileRegistration; }
	 */
	@Override
	public UserProfile updateUserProfile(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering updateUserProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit updateUserProfileById()");
		UserProfile userProfile = hybridRoleRightUserProfileDao.findAndModify(updateParams, tenantId);
		String userId = userProfile.getUserId();

		if (userProfile.getGroupIds() != null) {
			List<GroupProfile> groupProfiles = new ArrayList<GroupProfile>();
			userProfile.getGroupIds().stream().forEach(g -> {

				GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findById(g, tenantId);
				Set<String> groupProfileUserList = new HashSet<String>();
				if (groupProfile != null) {
					if (groupProfile.getUserIds() != null) {
						groupProfileUserList.addAll(groupProfile.getUserIds());
					}
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					JSONObject updateParams1 = new JSONObject(groupProfile);
					System.out.println("updateParams--->" + updateParams1);
					hybridRoleRightGroupProfileDao.findAndModify(g, updateParams1.toMap(), tenantId);

				} else {
					groupProfile = new GroupProfile();
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					groupProfile.setTenantId(tenantId);
					groupProfile.setGroupId(g);
					groupProfiles.add(groupProfile);
				}
			});
			if (groupProfiles.size() > 0)
				hybridRoleRightGroupProfileDao.saveAll(groupProfiles);
		}
		return userProfile;
	}

	@Override
	public GroupProfile registerGroup(GroupProfile groupProfile)
			throws JsonParseException, JsonMappingException, JSONException, IOException, CustomException {
		logger.debug("Entering register()");
		System.out.println(groupProfile.getTenantId());

		String tenantId = groupProfile.getTenantId();
	

		validRequest = true;
		if (groupProfile.getProfiles() != null) {
			groupProfile.getProfiles().stream().forEach(profile -> {
				if (profile.getObjectType().equalsIgnoreCase("folder")) {
					Folder folder = folderDao.findById(profile.getObjectId(), tenantId);
					System.out.println(folder);
					if (folder != null && folder.getParentFolderId() != null) {
						profile.setParentFolderId(folder.getParentFolderId());
					} else {
						validRequest = false;
					}
				} else if (profile.getObjectType().equalsIgnoreCase("content")) {
					Content content = contentDao.findOne(profile.getObjectId(), tenantId);
					if (content != null && content.getParentFolderId() != null) {
						profile.setParentFolderId(content.getParentFolderId());
					} else {
						validRequest = false;
					}
				}
			});
		}

		if (!validRequest) {
			exceptionThrower.throwInvalidObjectType();
		}

		// Create Secured Data object
		groupProfile = hybridRoleRightGroupProfileDao.save(groupProfile);

		String groupId = groupProfile.getGroupId();
		logger.debug("Entering register()");

		// Create Secured Data object
		// userGroupAssociation =
		// hybridRoleRightUserGroupAssociationDao.save(userGroupAssociation);

		if (groupProfile.getUserIds() != null) {
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			groupProfile.getUserIds().stream().forEach(u -> {

				UserProfile userProfile = hybridRoleRightUserProfileDao.findById(u, tenantId);
				Set<String> userProfileGroupList = new HashSet<String>();
				if (userProfile != null) {
					if (userProfile.getGroupIds() != null) {
						userProfileGroupList.addAll(userProfile.getGroupIds());
					}
					userProfileGroupList.add(groupId);
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					JSONObject updateParams = new JSONObject(userProfile);
					System.out.println("updateParams--->" + updateParams);
					try {
						hybridRoleRightUserProfileDao.findAndModify(updateParams.toString(), tenantId);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					userProfile = new UserProfile();
					userProfileGroupList.add(groupId);
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					userProfile.setTenantId(tenantId);
					userProfile.setUserId(u);
					userProfiles.add(userProfile);
				}
			});
			if (userProfiles.size() > 0)
				hybridRoleRightUserProfileDao.saveAll(userProfiles);
		}
		logger.debug("Exit registerGroup()");
		return groupProfile;
	}

	@Override
	public List<GroupProfile> registerBulkGroups(List<GroupProfile> groupProfile, String tenantId)
			throws JSONException {
		logger.debug("Entering registerBulk()");
		// System.out.println(groupProfile.getTenantId());
		// Create Secured Data object
		groupProfile = hybridRoleRightGroupProfileDao.saveAll(groupProfile);

		if (groupProfile != null) {
			groupProfile.stream().forEach(group -> {
				List<UserProfile> userProfiles = new ArrayList<UserProfile>();
				if (group.getUserIds() != null) {
					group.getUserIds().stream().forEach(u -> {

						UserProfile userProfile = hybridRoleRightUserProfileDao.findById(u, tenantId);
						Set<String> userProfileGroupList = new HashSet<String>();
						if (userProfile != null) {
							if (userProfile.getGroupIds() != null) {
								userProfileGroupList.addAll(userProfile.getGroupIds());
							}
							userProfileGroupList.add(group.getGroupId());
							userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
							JSONObject updatedParams = new JSONObject(userProfile);
							System.out.println("updateParams--->" + updatedParams);
							try {
								hybridRoleRightUserProfileDao.findAndModify(updatedParams.toString(), tenantId);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							userProfile = new UserProfile();
							userProfileGroupList.add(group.getGroupId());
							userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
							userProfile.setTenantId(tenantId);
							userProfile.setUserId(u);
							userProfiles.add(userProfile);
						}
					});
					if (userProfiles.size() > 0)
						hybridRoleRightUserProfileDao.saveAll(userProfiles);
				}
			});
		}
		logger.debug("Exit registerBulk()");
		return groupProfile;
	}

	@Override
	public List<GroupProfile> listGroups(String tenantId) {
		logger.debug("Entering list()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		List<GroupProfile> groupProfileList = hybridRoleRightGroupProfileDao.findAll(null, tenantId);
		logger.debug("Exit list()");
		return groupProfileList;
	}

	@Override
	public GroupProfile readGroupProfileById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readGroupProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readGroupProfileById()");
		return hybridRoleRightGroupProfileDao.findById(id, tenantId);
	}

	@Override
	public List<GroupProfile> searchGroups(Map<String, String[]> updateParams, String tenantId) throws JSONException {
		logger.debug("Entering search()");
		List<GroupProfile> groupProfiles = hybridRoleRightGroupProfileDao.findAll(updateParams, tenantId);

		/*
		 * groupProfiles.stream().forEach(groupProfile -> { if
		 * (groupProfile.getProfile() != null) { List<com.newgen.model.Profile>
		 * neededProfile = groupProfile.getProfile().stream() .filter(profile ->
		 * (updateParams.containsKey("profile.parentFolderId") &&
		 * updateParams.get("profile.parentFolderId").length > 0 &&
		 * profile.getParentFolderId() != null &&
		 * updateParams.get("profile.parentFolderId")[0].equals(profile.
		 * getParentFolderId())) ||
		 * (!updateParams.containsKey("profile.parentFolderId")))
		 * .collect(Collectors.toList()); groupProfile.setProfile(null);
		 * groupProfile.setProfile(neededProfile);
		 * 
		 * } });
		 */
		return groupProfiles;
	}

	@Override
	public GroupProfile deleteGroupProfile(String id, String tenantId) throws JSONException {
		logger.debug("Entering deleteGroupProfile()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findAndRemoveById(id, tenantId);

		if (groupProfile.getUserIds() != null) {
			groupProfile.getUserIds().stream().forEach(u -> {

				UserProfile userProfile = hybridRoleRightUserProfileDao.findById(u, tenantId);
				Set<String> userProfileGroupList = new HashSet<String>();
				if (userProfile != null) {
					if (userProfile.getGroupIds() != null) {
						userProfileGroupList.addAll(userProfile.getGroupIds());
					}
					userProfileGroupList.remove(groupProfile.getGroupId());
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					JSONObject updatedParams = new JSONObject(userProfile);
					System.out.println("updateParams--->" + updatedParams);
					try {
						hybridRoleRightUserProfileDao.findAndModify(updatedParams.toString(), tenantId);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		return groupProfile;
	}

	@Override
	public GroupProfile updateGroupProfileById(Map<String, String> updateParams, String id, String tenantId) {
		logger.debug("Entering updateGroupProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit updateGroupProfileById()");
		return null;// hybridRoleRightGroupProfileDao.findAndModify(id, updateParams, tenantId);
	}

	// @Override
	/*
	 * public GroupProfile updateGroupProfile1(Map<String, Object> updateParams,
	 * String groupId, String tenantId) {
	 * logger.debug("Entering updateGroupProfile()");
	 * 
	 * System.out.println("abccccccccccccccccccccccccccccccccccccccccccccccc");
	 * System.out.println(updateParams); // String url =
	 * eurekaUrlResolver.procureUrl(kmsServiceId); GroupProfile
	 * groupProfileRegistration =
	 * hybridRoleRightGroupProfileDao.findAndModifyByGroup(groupId, updateParams,
	 * tenantId); // (groupId, updateParams, // tenantId); if
	 * (groupProfileRegistration == null) { groupProfileRegistration =
	 * hybridRoleRightGroupProfileDao.findByGroup(groupId, tenantId); }
	 * logger.debug("Exit updateGroupProfile()"); return groupProfileRegistration; }
	 */
	@Override
	public GroupProfile updateGroupProfile(GroupProfileDTO groupProfileDTO, String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException, CustomException {
		logger.debug("Entering updateGroupProfile()");

		// String tenantId = groupProfile.getTenantId();
		// String groupId = groupProfileDTO.getGroupId();

		validRequest = true;
		if (groupProfileDTO != null) {
			if (groupProfileDTO.getProfiles() != null) {
				groupProfileDTO.getProfiles().stream().forEach(profile -> {
					if (profile.getObjectType().equalsIgnoreCase("folder")) {
						Folder folder = folderDao.findById(profile.getObjectId(), tenantId);
						System.out.println(folder);
						if (folder != null && folder.getParentFolderId() != null) {
							profile.setParentFolderId(folder.getParentFolderId());
						} else {
							validRequest = false;
						}
					} else if (profile.getObjectType().equalsIgnoreCase("content")) {
						Content content = contentDao.findOne(profile.getObjectId(), tenantId);
						if (content != null && content.getParentFolderId() != null) {
							profile.setParentFolderId(content.getParentFolderId());
						} else {
							validRequest = false;
						}
					}
				});
			}
		}
		if (!validRequest) {
			exceptionThrower.throwInvalidObjectType();
		}

		if (groupProfileDTO != null) {
			JSONObject json = new JSONObject(groupProfileDTO);
			updateParams = json.toString();
		}

		System.out.println("updateParams---->" + updateParams);
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		GroupProfile updatedGroupProfile = hybridRoleRightGroupProfileDao.findAndModifyByGroup(updateParams, tenantId);

		if (updatedGroupProfile.getUserIds() != null) {
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			updatedGroupProfile.getUserIds().stream().forEach(u -> {

				UserProfile userProfile = hybridRoleRightUserProfileDao.findById(u, tenantId);
				Set<String> userProfileGroupList = new HashSet<String>();
				if (userProfile != null) {
					if (userProfile.getGroupIds() != null) {
						userProfileGroupList.addAll(userProfile.getGroupIds());
					}
					userProfileGroupList.add(updatedGroupProfile.getGroupId());
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					JSONObject updatedParams = new JSONObject(userProfile);
					System.out.println("updateParams--->" + updatedParams);
					try {
						hybridRoleRightUserProfileDao.findAndModify(updatedParams.toString(), tenantId);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					userProfile = new UserProfile();
					userProfileGroupList.add(updatedGroupProfile.getGroupId());
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					userProfile.setTenantId(tenantId);
					userProfile.setUserId(u);
					userProfiles.add(userProfile);
				}
			});
			if (userProfiles.size() > 0)
				hybridRoleRightUserProfileDao.saveAll(userProfiles);
		}
		// (groupId, updateParams,
		// tenantId);
		/*
		 * if (groupProfileRegistration == null) { groupProfileRegistration =
		 * hybridRoleRightGroupProfileDao.findByGroup(tenantId); }
		 */
		logger.debug("Exit updateGroupProfile()");
		return updatedGroupProfile;
	}

	// @Override
	public UserGroupAssociation register1(UserGroupAssociation userGroupAssociation) throws JSONException {
		logger.debug("Entering register()");
		String tenantId = userGroupAssociation.getTenantId();
		String groupId = userGroupAssociation.getGroupId();
		System.out.println(userGroupAssociation.getTenantId());
		// Create Secured Data object
		userGroupAssociation = hybridRoleRightUserGroupAssociationDao.save(userGroupAssociation);

		if (userGroupAssociation.getUserIds() != null) {
			List<GroupUserAssociation> groupUserAssociations = new ArrayList<GroupUserAssociation>();
			userGroupAssociation.getUserIds().stream().forEach(u -> {

				GroupUserAssociation groupUserAssociation = hybridRoleRightGroupUserAssociationDao.findById(u,
						tenantId);
				Set<String> groupsOfUser = new HashSet<String>();
				if (groupUserAssociation != null) {
					if (groupUserAssociation.getGroupIds() != null) {
						groupsOfUser.addAll(groupUserAssociation.getGroupIds());
					}
					groupsOfUser.add(groupId);
					groupUserAssociation.setGroupIds(new ArrayList<String>(groupsOfUser));
					hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(u, groupUserAssociation, tenantId);
				} else {
					groupUserAssociation = new GroupUserAssociation();
					groupsOfUser.add(groupId);
					groupUserAssociation.setGroupIds(new ArrayList<String>(groupsOfUser));
					groupUserAssociation.setTenantId(tenantId);
					groupUserAssociation.setUserId(u);
					groupUserAssociations.add(groupUserAssociation);
				}
				/*
				 * GroupUserAssociation groupUserAssociation = new
				 * GroupUserAssociation(tenantId, u, Arrays.asList(groupId));
				 * groupUserAssociations.add(groupUserAssociation);
				 */
			});
			if (groupUserAssociations.size() > 0)
				hybridRoleRightGroupUserAssociationDao.saveAll(groupUserAssociations);
		}
		logger.debug("Exit register()");
		return userGroupAssociation;
	}

	@Override
	public UserGroupAssociation register(UserGroupAssociation userGroupAssociation) throws JSONException {
		logger.debug("Entering register()");
		String tenantId = userGroupAssociation.getTenantId();
		String groupId = userGroupAssociation.getGroupId();
		System.out.println(userGroupAssociation.getTenantId());
		// Create Secured Data object
		userGroupAssociation = hybridRoleRightUserGroupAssociationDao.save(userGroupAssociation);

		if (userGroupAssociation.getUserIds() != null) {
			List<GroupUserAssociation> groupUserAssociations = new ArrayList<GroupUserAssociation>();
			userGroupAssociation.getUserIds().stream().forEach(u -> {

				GroupUserAssociation groupUserAssociation = hybridRoleRightGroupUserAssociationDao.findById(u,
						tenantId);
				Set<String> groupsOfUser = new HashSet<String>();
				if (groupUserAssociation != null) {
					if (groupUserAssociation.getGroupIds() != null) {
						groupsOfUser.addAll(groupUserAssociation.getGroupIds());
					}
					groupsOfUser.add(groupId);
					groupUserAssociation.setGroupIds(new ArrayList<String>(groupsOfUser));
					hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(u, groupUserAssociation, tenantId);
				} else {
					groupUserAssociation = new GroupUserAssociation();
					groupsOfUser.add(groupId);
					groupUserAssociation.setGroupIds(new ArrayList<String>(groupsOfUser));
					groupUserAssociation.setTenantId(tenantId);
					groupUserAssociation.setUserId(u);
					groupUserAssociations.add(groupUserAssociation);
				}
				/*
				 * GroupUserAssociation groupUserAssociation = new
				 * GroupUserAssociation(tenantId, u, Arrays.asList(groupId));
				 * groupUserAssociations.add(groupUserAssociation);
				 */
			});
			if (groupUserAssociations.size() > 0)
				hybridRoleRightGroupUserAssociationDao.saveAll(groupUserAssociations);
		}
		logger.debug("Exit register()");
		return userGroupAssociation;
	}

	@Override
	public List<UserGroupAssociation> registerBulk(List<UserGroupAssociation> userGroupAssociation)
			throws JSONException {
		logger.debug("Entering registerBulk()");
		// System.out.println(userGroupAssociation.getTenantId());
		// Create Secured Data object
		userGroupAssociation = hybridRoleRightUserGroupAssociationDao.saveAll(userGroupAssociation);
		logger.debug("Exit registerBulk()");
		return userGroupAssociation;
	}

	@Override
	public List<UserGroupAssociation> list(String tenantId) {
		logger.debug("Entering list()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		List<UserGroupAssociation> userGroupAssociationList = hybridRoleRightUserGroupAssociationDao.findAll(null,
				tenantId);
		logger.debug("Exit list()");
		return userGroupAssociationList;
	}

	@Override
	public UserGroupAssociation readUserGroupAssociationById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserGroupAssociationById()");
		return hybridRoleRightUserGroupAssociationDao.findById(id, tenantId);
	}

	@Override
	public GroupUserAssociation readGroupUserAssociationById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserGroupAssociationById()");
		return hybridRoleRightGroupUserAssociationDao.findById(id, tenantId);
	}

	@Override
	public UserGroupAssociation deleteUserGroupAssociation(String id, String tenantId) throws JSONException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		return hybridRoleRightUserGroupAssociationDao.findAndRemoveById(id, tenantId);
	}

	@Override
	public GroupProfile deallocateUserFromGroup(String tenantId, String groupId, String[] userIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findAndDeallocateUserFromGroupId(tenantId, groupId,
				userIds);

		if (groupProfile.getUserIds() != null) {
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			groupProfile.getUserIds().stream().forEach(u -> {

				UserProfile userProfile = hybridRoleRightUserProfileDao.findById(u, tenantId);
				Set<String> userProfileGroupList = new HashSet<String>();
				if (userProfile != null) {
					if (userProfile.getGroupIds() != null) {
						userProfileGroupList.addAll(userProfile.getGroupIds());
					}
					userProfileGroupList.remove(groupProfile.getGroupId());
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					JSONObject updatedParams = new JSONObject(userProfile);
					System.out.println("updateParams--->" + updatedParams);
					try {
						hybridRoleRightUserProfileDao.findAndModify(updatedParams.toString(), tenantId);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					userProfile = new UserProfile();
					userProfileGroupList.remove(groupProfile.getGroupId());
					userProfile.setGroupIds(new ArrayList<String>(userProfileGroupList));
					userProfile.setTenantId(tenantId);
					userProfile.setUserId(u);
					userProfiles.add(userProfile);
				}
			});
			if (userProfiles.size() > 0)
				hybridRoleRightUserProfileDao.saveAll(userProfiles);
		}
		return groupProfile;
	}

	@Override
	public UserProfile deallocateGroupFromUser(String tenantId, String userId, String[] groupIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		UserProfile userProfile = hybridRoleRightUserProfileDao.findAndDeallocateGroupFromUserId(tenantId, userId,
				groupIds);

		if (userProfile.getGroupIds() != null) {
			List<GroupProfile> groupProfiles = new ArrayList<GroupProfile>();
			userProfile.getGroupIds().stream().forEach(g -> {

				GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findById(g, tenantId);
				Set<String> groupProfileUserList = new HashSet<String>();
				if (groupProfile != null) {
					if (groupProfile.getUserIds() != null) {
						groupProfileUserList.addAll(groupProfile.getUserIds());
					}
					groupProfileUserList.remove(userProfile.getUserId());
					groupProfile.setUserIds((new ArrayList<String>(groupProfileUserList)));
					JSONObject updatedParams = new JSONObject(groupProfile);
					System.out.println("updateParams--->" + updatedParams);
					hybridRoleRightGroupProfileDao.findAndModify(g, updatedParams.toMap(), tenantId);

				} else {
					groupProfile = new GroupProfile();
					groupProfileUserList.remove(userProfile.getUserId());
					groupProfile.setUserIds((new ArrayList<String>(groupProfileUserList)));
					groupProfile.setTenantId(tenantId);
					groupProfile.setGroupId(g);
					groupProfiles.add(groupProfile);
				}
			});
			if (groupProfiles.size() > 0)
				hybridRoleRightGroupProfileDao.saveAll(groupProfiles);
		}
		return userProfile;
	}

	@Override
	public GroupProfile deallocateProfileFromGroup(String tenantId, String groupId, String[] objectIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findAndDeallocateProfileFromGroupId(tenantId,
				groupId, objectIds);

		return groupProfile;
	}

	@Override
	public UserProfile deallocateProfileFromUser(String tenantId, String userId, String[] objectIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		UserProfile userProfile = hybridRoleRightUserProfileDao.findAndDeallocateProfileFromUserId(tenantId, userId,
				objectIds);

		return userProfile;
	}

	@Override
	public UserGroupAssociation updateUserGroupAssociationById(Map<String, Object> updateParams, String id,
			String tenantId) {
		logger.debug("Entering updateUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit updateUserGroupAssociationById()");
		return hybridRoleRightUserGroupAssociationDao.findAndModify(id, updateParams, tenantId);
	}

	@Override
	public GroupProfile updateGroupProfile(String updateParams, String groupId, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering updateGroupProfile()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		GroupProfile groupProfile = hybridRoleRightGroupProfileDao.findAndModifyByGroup(updateParams, tenantId);
		// (userId, updateParams,
		// tenantId);
		if (groupProfile == null) {
			groupProfile = hybridRoleRightGroupProfileDao.findByGroup(groupId, tenantId);
		}
		logger.debug("Exit updateGroupProfile()");
		return groupProfile;
	}
}
