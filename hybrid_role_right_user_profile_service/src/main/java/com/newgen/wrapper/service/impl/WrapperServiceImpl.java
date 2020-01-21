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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

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
import com.newgen.dto.UserProfileDTO;
import com.newgen.exception.CustomException;
import com.newgen.model.Content;
import com.newgen.model.Folder;
import com.newgen.model.GroupProfile;
import com.newgen.model.GroupUserAssociation;
import com.newgen.model.InOutParameters;
import com.newgen.model.LogEntity;
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

	@Autowired
	RestTemplate restTemplate;

	@Value("${logging.service.url}")
	private String loggingServiceUrl;

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
					long startTime = System.nanoTime();
					InOutParameters inOutParameters = folderDao.findById(profile.getObjectId(), tenantId);
					long endTime = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
							inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
							"UserGroupService");
					Folder folder = inOutParameters.getFolder();
					System.out.println(folder);
					if (folder != null && folder.getParentFolderId() != null) {
						profile.setParentFolderId(folder.getParentFolderId());
					} else {
						validRequest = false;
					}
				} else if (profile.getObjectType().equalsIgnoreCase("content")) {
					long startTime = System.nanoTime();
					InOutParameters inOutParameters = contentDao.findOne(profile.getObjectId(), tenantId);
					long endTime = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
							inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
							"UserGroupService");
					Content content = inOutParameters.getContent();
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

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = hybridRoleRightUserProfileDao.save(userProfile);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "POST", "UserGroupService");

		userProfile = inOutParameters.getUserProfile();

		if (userProfile.getGroupIds() != null) {
			List<GroupProfile> groupProfiles = new ArrayList<GroupProfile>();
			userProfile.getGroupIds().stream().forEach(g -> {

				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findById(g, tenantId);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParameters.getRequestPayloadSize(),
						inOutParameters.getResponsePayloadSize(), "GET", "UserGroupService");

				GroupProfile groupProfile = inOutParams.getGroupProfile();
				Set<String> groupProfileUserList = new HashSet<String>();
				if (groupProfile != null) {
					if (groupProfile.getUserIds() != null) {
						groupProfileUserList.addAll(groupProfile.getUserIds());
					}
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					JSONObject updateParams = new JSONObject(groupProfile);
					System.out.println("updateParams--->" + updateParams);
					long st = System.nanoTime();
					InOutParameters inout = hybridRoleRightGroupProfileDao.findAndModify(g, updateParams.toMap(),
							tenantId);
					long et = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
							inout.getResponsePayloadSize(), "PUT", "UserGroupService");

				} else {
					groupProfile = new GroupProfile();
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					groupProfile.setTenantId(tenantId);
					groupProfile.setGroupId(g);
					groupProfiles.add(groupProfile);
				}
			});
			if (groupProfiles.size() > 0) {
				long st = System.nanoTime();
				InOutParameters inout = hybridRoleRightGroupProfileDao.saveAll(groupProfiles);
				long et = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
						inout.getResponsePayloadSize(), "POST", "UserGroupService");
			}

		}
		logger.debug("Exit register()");
		return userProfile;
	}

	@Override
	public List<UserProfile> registerBulkUsers(UserProfileDTO userProfileDTO, List<UserProfile> userProfile,
			String tenantId) throws JSONException, CustomException {
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
						response.add(updateUserProfile(userProfileDTO, json.toString(), tenantId));
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CustomException e) {
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

		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightUserProfileDao.findAll(null, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");

		List<UserProfile> userProfileList = inOutParams.getUserProfileList();
		logger.debug("Exit list()");
		return userProfileList;
	}

	@Override
	public UserProfile readUserProfileById(String id, String tenantId) {
		logger.debug("Entering readUserProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserProfileById()");

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findById(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "UserGroupService");

		return inOutParameters.getUserProfile();
	}

	@Override
	public UserProfile readUserProfileFavouritesById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readUserProfileFavouritesById()");

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findFavouritesById(id, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "UserGroupService");

		UserProfile userProfile = inOutParameters.getUserProfile();
		List<com.newgen.model.Profile> favouriteProfile = userProfile.getProfiles().stream()
				.filter(profile -> profile.isFavourite() == true).collect(Collectors.toList());
		userProfile.setProfiles(favouriteProfile);
		return userProfile;
	}

	@Override
	public List<UserProfile> searchUsers(Map<String, String[]> allRequestParams, String tenantId) {
		logger.debug("Entering search()");

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findAll(allRequestParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "UserGroupService");

		List<UserProfile> userProfiles = inOutParameters.getUserProfileList();

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

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findAllFavourites(allRequestParams, tenantId);
		long endTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", startTime, endTime, inOutParameters.getRequestPayloadSize(),
				inOutParameters.getResponsePayloadSize(), "GET", "UserGroupService");

		List<UserProfile> userProfiles = inOutParameters.getUserProfileList();

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

		long sTime = System.nanoTime();
		InOutParameters inOutParam = hybridRoleRightUserProfileDao.findAndRemoveById(id, tenantId);
		long eTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOutParam.getRequestPayloadSize(),
				inOutParam.getResponsePayloadSize(), "DELETE", "UserGroupService");

		UserProfile userProfile = inOutParam.getUserProfile();

		if (userProfile != null) {
			if (userProfile.getGroupIds() != null) {
				userProfile.getGroupIds().stream().forEach(g -> {

					long sT = System.nanoTime();
					InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findById(g, tenantId);
					long eT = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
							inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");

					GroupProfile groupProfile = inOutParams.getGroupProfile();
					Set<String> groupProfileUserList = new HashSet<String>();
					if (groupProfile != null) {
						if (groupProfile.getUserIds() != null) {
							groupProfileUserList.addAll(groupProfile.getUserIds());
						}
						groupProfileUserList.remove(userProfile.getUserId());
						groupProfile.setUserIds((new ArrayList<String>(groupProfileUserList)));
						JSONObject updatedParams = new JSONObject(groupProfile);
						System.out.println("updateParams--->" + updatedParams);

						long sTym = System.nanoTime();
						InOutParameters inOut = hybridRoleRightGroupProfileDao.findAndModify(g, updatedParams.toMap(),
								tenantId);
						long eTym = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", sTym, eTym, inOut.getRequestPayloadSize(),
								inOutParams.getResponsePayloadSize(), "PUT", "UserGroupService");
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
	public UserProfile updateUserProfile(UserProfileDTO userProfileDTO, String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException, CustomException {
		logger.debug("Entering updateUserProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit updateUserProfileById()");

		validRequest = true;
		if (userProfileDTO != null) {
			if (userProfileDTO.getProfiles() != null) {
				userProfileDTO.getProfiles().stream().forEach(profile -> {
					if (profile.getObjectType().equalsIgnoreCase("folder")) {
						long startTime = System.nanoTime();
						InOutParameters inOutParameters = folderDao.findById(profile.getObjectId(), tenantId);
						long endTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
								inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(),
								"GET", "UserGroupService");
						Folder folder = inOutParameters.getFolder();
						System.out.println(folder);
						if (folder != null && folder.getParentFolderId() != null) {
							profile.setParentFolderId(folder.getParentFolderId());
						} else {
							validRequest = false;
						}
					} else if (profile.getObjectType().equalsIgnoreCase("content")) {

						long startTime = System.nanoTime();
						InOutParameters inOutParameters = contentDao.findOne(profile.getObjectId(), tenantId);
						long endTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
								inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(),
								"GET", "UserGroupService");

						Content content = inOutParameters.getContent();
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

		if (userProfileDTO != null) {
			JSONObject json = new JSONObject(userProfileDTO);
			updateParams = json.toString();
		}

		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightUserProfileDao.findAndModify(updateParams, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "PUT", "UserGroupService");

		UserProfile userProfile = inOutParams.getUserProfile();
		String userId = userProfile.getUserId();

		if (userProfile.getGroupIds() != null) {
			List<GroupProfile> groupProfiles = new ArrayList<GroupProfile>();
			userProfile.getGroupIds().stream().forEach(g -> {

				long sTime = System.nanoTime();
				InOutParameters inOutParam = hybridRoleRightGroupProfileDao.findById(g, tenantId);
				long eTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOutParam.getRequestPayloadSize(),
						inOutParam.getResponsePayloadSize(), "GET", "UserGroupService");

				GroupProfile groupProfile = inOutParam.getGroupProfile();
				Set<String> groupProfileUserList = new HashSet<String>();
				if (groupProfile != null) {
					if (groupProfile.getUserIds() != null) {
						groupProfileUserList.addAll(groupProfile.getUserIds());
					}
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					JSONObject updateParams1 = new JSONObject(groupProfile);
					System.out.println("updateParams--->" + updateParams1);
					long st = System.nanoTime();
					InOutParameters inout = hybridRoleRightGroupProfileDao.findAndModify(g, updateParams1.toMap(),
							tenantId);
					long et = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
							inout.getResponsePayloadSize(), "PUT", "UserGroupService");

				} else {
					groupProfile = new GroupProfile();
					groupProfileUserList.add(userId);
					groupProfile.setUserIds(new ArrayList<String>(groupProfileUserList));
					groupProfile.setTenantId(tenantId);
					groupProfile.setGroupId(g);
					groupProfiles.add(groupProfile);
				}
			});
			if (groupProfiles.size() > 0) {
				long st = System.nanoTime();
				InOutParameters inout = hybridRoleRightGroupProfileDao.saveAll(groupProfiles);
				long et = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
						inout.getResponsePayloadSize(), "POST", "UserGroupService");

			}

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

					long startTime = System.nanoTime();
					InOutParameters inOutParameters = folderDao.findById(profile.getObjectId(), tenantId);
					long endTime = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
							inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
							"UserGroupService");

					Folder folder = inOutParameters.getFolder();
					System.out.println(folder);
					if (folder != null && folder.getParentFolderId() != null) {
						profile.setParentFolderId(folder.getParentFolderId());
					} else {
						validRequest = false;
					}
				} else if (profile.getObjectType().equalsIgnoreCase("content")) {
					long startTime = System.nanoTime();
					InOutParameters inOutParameters = contentDao.findOne(profile.getObjectId(), tenantId);
					long endTime = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
							inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
							"UserGroupService");
					Content content = inOutParameters.getContent();
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
		long st = System.nanoTime();
		InOutParameters inout = hybridRoleRightGroupProfileDao.save(groupProfile);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
				inout.getResponsePayloadSize(), "POST", "UserGroupService");

		groupProfile = inout.getGroupProfile();

		String groupId = groupProfile.getGroupId();
		logger.debug("Entering register()");

		// Create Secured Data object
		// userGroupAssociation =
		// hybridRoleRightUserGroupAssociationDao.save(userGroupAssociation);

		if (groupProfile.getUserIds() != null) {
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			groupProfile.getUserIds().stream().forEach(u -> {
				long startTime = System.nanoTime();
				InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findById(u, tenantId);
				long endTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");
				UserProfile userProfile = inOutParameters.getUserProfile();
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

						long sT = System.nanoTime();
						InOutParameters inOutParams = hybridRoleRightUserProfileDao
								.findAndModify(updateParams.toString(), tenantId);
						long eT = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
								inOutParams.getResponsePayloadSize(), "PUT", "UserGroupService");
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
			if (userProfiles.size() > 0) {
				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightUserProfileDao.saveAll(userProfiles);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
						inOutParams.getResponsePayloadSize(), "POST", "UserGroupService");
			}
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
		long st = System.nanoTime();
		InOutParameters inOut = hybridRoleRightGroupProfileDao.saveAll(groupProfile);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inOut.getRequestPayloadSize(),
				inOut.getResponsePayloadSize(), "POST", "UserGroupService");

		groupProfile = inOut.getGroupProfileList();

		if (groupProfile != null) {
			groupProfile.stream().forEach(group -> {
				List<UserProfile> userProfiles = new ArrayList<UserProfile>();
				if (group.getUserIds() != null) {
					group.getUserIds().stream().forEach(u -> {

						long startTime = System.nanoTime();
						InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findById(u, tenantId);
						long endTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
								inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(),
								"GET", "UserGroupService");
						UserProfile userProfile = inOutParameters.getUserProfile();
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
								long sTime = System.nanoTime();
								InOutParameters inout = hybridRoleRightUserProfileDao
										.findAndModify(updatedParams.toString(), tenantId);
								long eTime = System.nanoTime();
								callLoggingService(tenantId, null, "CosmosDB", sTime, eTime,
										inout.getRequestPayloadSize(), inout.getResponsePayloadSize(), "PUT",
										"UserGroupService");
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
					if (userProfiles.size() > 0) {
						long sTime = System.nanoTime();
						InOutParameters inout = hybridRoleRightUserProfileDao.saveAll(userProfiles);
						long eTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inout.getRequestPayloadSize(),
								inout.getResponsePayloadSize(), "POST", "UserGroupService");
					}

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
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findAll(null, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
		List<GroupProfile> groupProfileList = inOutParams.getGroupProfileList();
		logger.debug("Exit list()");
		return groupProfileList;
	}

	@Override
	public GroupProfile readGroupProfileById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readGroupProfileById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readGroupProfileById()");
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findById(id, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
		return inOutParams.getGroupProfile();
	}

	@Override
	public List<GroupProfile> searchGroups(Map<String, String[]> updateParams, String tenantId) throws JSONException {
		logger.debug("Entering search()");
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findAll(updateParams, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
		List<GroupProfile> groupProfiles = inOutParams.getGroupProfileList();

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
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findAndRemoveById(id, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "DELETE", "UserGroupService");
		GroupProfile groupProfile = inOutParams.getGroupProfile();

		if (groupProfile.getUserIds() != null) {
			groupProfile.getUserIds().stream().forEach(u -> {

				long startTime = System.nanoTime();
				InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findById(u, tenantId);
				long endTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");

				UserProfile userProfile = inOutParameters.getUserProfile();
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
						long sTime = System.nanoTime();
						InOutParameters inout = hybridRoleRightUserProfileDao.findAndModify(updatedParams.toString(),
								tenantId);
						long eTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inout.getRequestPayloadSize(),
								inout.getResponsePayloadSize(), "PUT", "UserGroupService");

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
						long startTime = System.nanoTime();
						InOutParameters inOutParameters = folderDao.findById(profile.getObjectId(), tenantId);
						long endTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
								inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(),
								"GET", "UserGroupService");
						Folder folder = inOutParameters.getFolder();
						System.out.println(folder);
						if (folder != null && folder.getParentFolderId() != null) {
							profile.setParentFolderId(folder.getParentFolderId());
						} else {
							validRequest = false;
						}
					} else if (profile.getObjectType().equalsIgnoreCase("content")) {
						long startTime = System.nanoTime();
						InOutParameters inOutParameters = contentDao.findOne(profile.getObjectId(), tenantId);
						long endTime = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
								inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(),
								"GET", "UserGroupService");
						Content content = inOutParameters.getContent();
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
		long st = System.nanoTime();
		InOutParameters inout = hybridRoleRightGroupProfileDao.findAndModifyByGroup(updateParams, tenantId);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
				inout.getResponsePayloadSize(), "PUT", "UserGroupService");
		GroupProfile updatedGroupProfile = inout.getGroupProfile();

		if (updatedGroupProfile.getUserIds() != null) {
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			updatedGroupProfile.getUserIds().stream().forEach(u -> {

				long startTime = System.nanoTime();
				InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findById(u, tenantId);
				long endTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");

				UserProfile userProfile = inOutParameters.getUserProfile();
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
						long sT = System.nanoTime();
						InOutParameters inOutParams = hybridRoleRightUserProfileDao
								.findAndModify(updatedParams.toString(), tenantId);
						long eT = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
								inOutParams.getResponsePayloadSize(), "PUT", "UserGroupService");

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
			if (userProfiles.size() > 0) {

				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightUserProfileDao.saveAll(userProfiles);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
						inOutParams.getResponsePayloadSize(), "POST", "UserGroupService");
			}

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
		long st = System.nanoTime();
		InOutParameters inout = hybridRoleRightUserGroupAssociationDao.save(userGroupAssociation);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
				inout.getResponsePayloadSize(), "POST", "UserGroupService");
		userGroupAssociation = inout.getUserGroupAssociation();

		if (userGroupAssociation.getUserIds() != null) {
			List<GroupUserAssociation> groupUserAssociations = new ArrayList<GroupUserAssociation>();
			userGroupAssociation.getUserIds().stream().forEach(u -> {

				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightGroupUserAssociationDao.findById(u, tenantId);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
						inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
				GroupUserAssociation groupUserAssociation = inOutParams.getGroupUserAssociation();
				Set<String> groupsOfUser = new HashSet<String>();
				if (groupUserAssociation != null) {
					if (groupUserAssociation.getGroupIds() != null) {
						groupsOfUser.addAll(groupUserAssociation.getGroupIds());
					}
					groupsOfUser.add(groupId);
					groupUserAssociation.setGroupIds(new ArrayList<String>(groupsOfUser));
					long sTime = System.nanoTime();
					InOutParameters inOut = hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(u,
							groupUserAssociation, tenantId);
					long eTime = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOut.getRequestPayloadSize(),
							inOut.getResponsePayloadSize(), "PUT", "UserGroupService");

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
			if (groupUserAssociations.size() > 0) {
				long sTime = System.nanoTime();
				InOutParameters inOut = hybridRoleRightGroupUserAssociationDao.saveAll(groupUserAssociations);
				long eTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOut.getRequestPayloadSize(),
						inOut.getResponsePayloadSize(), "POST", "UserGroupService");
			}
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
		long stTime = System.nanoTime();
		InOutParameters inOutPara = hybridRoleRightUserGroupAssociationDao.save(userGroupAssociation);
		long etTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", stTime, etTime, inOutPara.getRequestPayloadSize(),
				inOutPara.getResponsePayloadSize(), "POST", "UserGroupService");
		userGroupAssociation = inOutPara.getUserGroupAssociation();

		if (userGroupAssociation.getUserIds() != null) {
			List<GroupUserAssociation> groupUserAssociations = new ArrayList<GroupUserAssociation>();
			userGroupAssociation.getUserIds().stream().forEach(u -> {

				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightGroupUserAssociationDao.findById(u, tenantId);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
						inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
				GroupUserAssociation groupUserAssociation = inOutParams.getGroupUserAssociation();
				Set<String> groupsOfUser = new HashSet<String>();
				if (groupUserAssociation != null) {
					if (groupUserAssociation.getGroupIds() != null) {
						groupsOfUser.addAll(groupUserAssociation.getGroupIds());
					}
					groupsOfUser.add(groupId);
					groupUserAssociation.setGroupIds(new ArrayList<String>(groupsOfUser));
					long sTime = System.nanoTime();
					InOutParameters inOut = hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(u,
							groupUserAssociation, tenantId);
					long eTime = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOut.getRequestPayloadSize(),
							inOut.getResponsePayloadSize(), "PUT", "UserGroupService");

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
			if (groupUserAssociations.size() > 0) {
				long sTime = System.nanoTime();
				InOutParameters inOut = hybridRoleRightGroupUserAssociationDao.saveAll(groupUserAssociations);
				long eTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOut.getRequestPayloadSize(),
						inOut.getResponsePayloadSize(), "POST", "UserGroupService");
			}

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
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightUserGroupAssociationDao.findAll(null, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
		List<UserGroupAssociation> userGroupAssociationList = inOutParams.getUserGroupAssociationList();
		logger.debug("Exit list()");
		return userGroupAssociationList;
	}

	@Override
	public UserGroupAssociation readUserGroupAssociationById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserGroupAssociationById()");
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightUserGroupAssociationDao.findById(id, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
		return inOutParams.getUserGroupAssociation();
	}

	@Override
	public GroupUserAssociation readGroupUserAssociationById(String id, String tenantId) throws JSONException {
		logger.debug("Entering readUserGroupAssociationById()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit readUserGroupAssociationById()");
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightGroupUserAssociationDao.findById(id, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");
		return inOutParams.getGroupUserAssociation();
	}

	@Override
	public UserGroupAssociation deleteUserGroupAssociation(String id, String tenantId) throws JSONException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightUserGroupAssociationDao.findAndRemoveById(id, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "DELETE", "UserGroupService");
		return inOutParams.getUserGroupAssociation();
	}

	@Override
	public GroupProfile deallocateUserFromGroup(String tenantId, String groupId, String[] userIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		long st = System.nanoTime();
		InOutParameters inout = hybridRoleRightGroupProfileDao.findAndDeallocateUserFromGroupId(tenantId, groupId,
				userIds);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
				inout.getResponsePayloadSize(), "DELETE", "UserGroupService");
		GroupProfile groupProfile = inout.getGroupProfile();

		if (groupProfile.getUserIds() != null) {
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			groupProfile.getUserIds().stream().forEach(u -> {

				long startTime = System.nanoTime();
				InOutParameters inOutParameters = hybridRoleRightUserProfileDao.findById(u, tenantId);
				long endTime = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");
				UserProfile userProfile = inOutParameters.getUserProfile();
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
						long sT = System.nanoTime();
						InOutParameters inOutParams = hybridRoleRightUserProfileDao
								.findAndModify(updatedParams.toString(), tenantId);
						long eT = System.nanoTime();
						callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
								inOutParams.getResponsePayloadSize(), "PUT", "UserGroupService");

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
			if (userProfiles.size() > 0) {
				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightUserProfileDao.saveAll(userProfiles);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
						inOutParams.getResponsePayloadSize(), "POST", "UserGroupService");
			}
		}
		return groupProfile;
	}

	@Override
	public UserProfile deallocateGroupFromUser(String tenantId, String userId, String[] groupIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		long sTime = System.nanoTime();
		InOutParameters inOutParam = hybridRoleRightUserProfileDao.findAndDeallocateGroupFromUserId(tenantId, userId,
				groupIds);
		long eTime = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOutParam.getRequestPayloadSize(),
				inOutParam.getResponsePayloadSize(), "DELETE", "UserGroupService");
		UserProfile userProfile = inOutParam.getUserProfile();

		if (userProfile.getGroupIds() != null) {
			List<GroupProfile> groupProfiles = new ArrayList<GroupProfile>();
			userProfile.getGroupIds().stream().forEach(g -> {

				long sT = System.nanoTime();
				InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findById(g, tenantId);
				long eT = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
						inOutParams.getResponsePayloadSize(), "GET", "UserGroupService");

				GroupProfile groupProfile = inOutParams.getGroupProfile();
				Set<String> groupProfileUserList = new HashSet<String>();
				if (groupProfile != null) {
					if (groupProfile.getUserIds() != null) {
						groupProfileUserList.addAll(groupProfile.getUserIds());
					}
					groupProfileUserList.remove(userProfile.getUserId());
					groupProfile.setUserIds((new ArrayList<String>(groupProfileUserList)));
					JSONObject updatedParams = new JSONObject(groupProfile);
					System.out.println("updateParams--->" + updatedParams);
					long st = System.nanoTime();
					InOutParameters inout = hybridRoleRightGroupProfileDao.findAndModify(g, updatedParams.toMap(),
							tenantId);
					long et = System.nanoTime();
					callLoggingService(tenantId, null, "CosmosDB", st, et, inout.getRequestPayloadSize(),
							inout.getResponsePayloadSize(), "PUT", "UserGroupService");

				} else {
					groupProfile = new GroupProfile();
					groupProfileUserList.remove(userProfile.getUserId());
					groupProfile.setUserIds((new ArrayList<String>(groupProfileUserList)));
					groupProfile.setTenantId(tenantId);
					groupProfile.setGroupId(g);
					groupProfiles.add(groupProfile);
				}
			});
			if (groupProfiles.size() > 0) {
				long st = System.nanoTime();
				InOutParameters inOut = hybridRoleRightGroupProfileDao.saveAll(groupProfiles);
				long et = System.nanoTime();
				callLoggingService(tenantId, null, "CosmosDB", st, et, inOut.getRequestPayloadSize(),
						inOut.getResponsePayloadSize(), "POST", "UserGroupService");
			}

		}
		return userProfile;
	}

	@Override
	public GroupProfile deallocateProfileFromGroup(String tenantId, String groupId, String[] objectIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		long st = System.nanoTime();
		InOutParameters inOut = hybridRoleRightGroupProfileDao.findAndDeallocateProfileFromGroupId(tenantId, groupId,
				objectIds);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inOut.getRequestPayloadSize(),
				inOut.getResponsePayloadSize(), "DELETE", "UserGroupService");
		GroupProfile groupProfile = inOut.getGroupProfile();

		return groupProfile;
	}

	@Override
	public UserProfile deallocateProfileFromUser(String tenantId, String userId, String[] objectIds)
			throws JSONException, CustomException {
		logger.debug("Entering deleteUserGroupAssociation()");
		// String url = eurekaUrlResolver.procureUrl(kmsServiceId);
		logger.debug("Exit deleteKMSCredential()");
		long st = System.nanoTime();
		InOutParameters inOut = hybridRoleRightUserProfileDao.findAndDeallocateProfileFromUserId(tenantId, userId,
				objectIds);
		long et = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", st, et, inOut.getRequestPayloadSize(),
				inOut.getResponsePayloadSize(), "DELETE", "UserGroupService");
		UserProfile userProfile = inOut.getUserProfile();

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
		long sT = System.nanoTime();
		InOutParameters inOutParams = hybridRoleRightGroupProfileDao.findAndModifyByGroup(updateParams, tenantId);
		long eT = System.nanoTime();
		callLoggingService(tenantId, null, "CosmosDB", sT, eT, inOutParams.getRequestPayloadSize(),
				inOutParams.getResponsePayloadSize(), "PUT", "UserGroupService");
		GroupProfile groupProfile = inOutParams.getGroupProfile();
		// (userId, updateParams,
		// tenantId);
		if (groupProfile == null) {
			long sTime = System.nanoTime();
			InOutParameters inOutParameters = hybridRoleRightGroupProfileDao.findByGroup(groupId, tenantId);
			long eTime = System.nanoTime();
			callLoggingService(tenantId, null, "CosmosDB", sTime, eTime, inOutParameters.getRequestPayloadSize(),
					inOutParameters.getResponsePayloadSize(), "GET", "UserGroupService");
			groupProfile = inOutParameters.getGroupProfile();
		}
		logger.debug("Exit updateGroupProfile()");
		return groupProfile;
	}

	public void callLoggingService(String tenantId, String userId, String logType, Long startTime, Long endTime,
			Double reqSize, Double resSize, String requestType, String serviceType) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("tenantId", tenantId);
		headers.set("userId", userId);
		headers.set("Content-Type", "application/json");

		String apiurl = loggingServiceUrl + "/logging/saveLog";
		if (reqSize != null) {
			reqSize = Math.ceil(reqSize);
		}
		if (resSize != null) {
			resSize = Math.ceil(resSize);
		}
		LogEntity logEntity = new LogEntity(logType, requestType, serviceType, reqSize, resSize, startTime, endTime);
		HttpEntity<LogEntity> request = new HttpEntity<LogEntity>(logEntity, headers);
		restTemplate.exchange(apiurl, HttpMethod.POST, request, String.class);
	}
}
