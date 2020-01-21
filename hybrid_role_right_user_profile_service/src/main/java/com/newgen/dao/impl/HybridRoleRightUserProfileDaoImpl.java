package com.newgen.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.comparator.ProfileComparator;
import com.newgen.controller.ExceptionThrower;
import com.newgen.dao.HybridRoleRightUserProfileDao;
import com.newgen.exception.CustomException;
import com.newgen.model.InOutParameters;
import com.newgen.model.Profile;
import com.newgen.model.UserProfile;
import com.newgen.repository.UserProfileRepository;
import com.newgen.wrapper.service.WrapperMongoService;
import com.newgen.wrapper.service.impl.WrapperServiceImpl;

@Repository
public class HybridRoleRightUserProfileDaoImpl implements HybridRoleRightUserProfileDao {
	private static final Logger logger = LoggerFactory.getLogger(HybridRoleRightUserProfileDaoImpl.class);

	@Autowired
	ExceptionThrower exceptionThrower;

	@Autowired
	UserProfileRepository userProfileRepository;

	@Autowired
	WrapperMongoService<UserProfile> mongoTemplate;

	@Autowired
	WrapperServiceImpl wrapperServiceImpl;

	@Override
	public UserProfile insert(UserProfile userProfile) {
		userProfile.setCreationDateTime(new Date());
		userProfile.setAccessDateTime(new Date());
		UserProfile userProfile2 = null;
		try {
			userProfile2 = userProfileRepository.insert(userProfile);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile2 == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile2 = userProfileRepository.insert(userProfile);
			}
			logger.debug(" " + userProfile2);
		}

		return userProfile2;
	}

	@Override
	public List<UserProfile> insertAll(List<UserProfile> userProfile) {
		userProfile.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		List<UserProfile> userProfiles = null;
		try {
			userProfiles = userProfileRepository.insert(userProfile);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfiles == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfiles = userProfileRepository.insert(userProfile);
			}
			logger.debug(" " + userProfiles);
		}
		return userProfiles;
	}

	@Override
	public InOutParameters saveAll(List<UserProfile> userProfile) {
		InOutParameters inOutParameters = new InOutParameters();
		userProfile.stream().forEach(u -> {
			u.setCreationDateTime(new Date());
			u.setAccessDateTime(new Date());
		});
		List<UserProfile> userProfiles = null;
		try {
			userProfiles = userProfileRepository.saveAll(userProfile);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfiles == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfiles = userProfileRepository.saveAll(userProfile);
			}
			logger.debug(" " + userProfiles);
		}
		Double resSize = 0.0;
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0);
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfiles).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setUserProfileList(userProfiles);
		return inOutParameters;
	}

	@Override
	public InOutParameters save(UserProfile userProfile) {
		UserProfile userProfile2 = null;
		InOutParameters inOutParameters = new InOutParameters();
		try {
			userProfile2 = userProfileRepository.save(userProfile);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile2 == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile2 = userProfileRepository.save(userProfile);
			}
			logger.debug(" " + userProfile2);
		}
		Double resSize = 0.0;
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0);
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile2).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setUserProfile(userProfile2);
		return inOutParameters;
	}

	public InOutParameters findById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findOne(query, UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findOne(query, UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}
		logger.debug("UserProfile found : " + userProfile);
		Double resSize = 0.0;
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setUserProfile(userProfile);
		return inOutParameters;
	}

	@Override
	public InOutParameters findFavouritesById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("profiles.favourite").is(true));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));

		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findOne(query, UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findOne(query, UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}
		logger.debug("UserProfile found : " + userProfile);
		Double resSize = 0.0;
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setUserProfile(userProfile);
		return inOutParameters;
	}

	@Override
	public UserProfile findByGroup(String group, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("groupId").is(group));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findOne(query, UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findOne(query, UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}
		return userProfile;
	}

	// @SuppressWarnings("unchecked")
	@Override
	public InOutParameters findAndRemoveById(String id, String tenantId) {
		Query query = new Query();
		InOutParameters inOutParameters = new InOutParameters();
		query.addCriteria(Criteria.where("_id").is(id));
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findAndRemove(query, UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findAndRemove(query, UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}
		logger.debug("Deleted userProfile : " + userProfile);
		Double resSize = 0.0;
		inOutParameters.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0;
		}
		inOutParameters.setResponsePayloadSize(resSize);
		inOutParameters.setUserProfile(userProfile);
		return inOutParameters;
	}

	@Override
	// @SuppressWarnings("unchecked")
	public InOutParameters findAndModify(String updateParams, String tenantId)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> updateParamsMap = mapper.readValue(updateParams, Map.class);

		Query query = new Query();
		InOutParameters inOutParams = new InOutParameters();
		String userId = updateParamsMap.get("userId").toString();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("userId").is(userId));

		// JSONObject json = new JSONObject(updateParamsJson);
		// Map<String, Object> map = json.toMap();

		/*
		 * IntStream.range(0,json.length()).forEach(counter->{ if
		 * ("id".equalsIgnoreCase(json.get.getKey()) ||
		 * "version".equalsIgnoreCase(entry.getKey()) ||
		 * "creationDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "accessDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "revisedDateTime".equalsIgnoreCase(entry.getKey())) { continue; }
		 * update.set(entry.getKey(), entry.getValue()); });
		 */
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		if (updateParams != null) {
			JSONObject updatedParamsJson = new JSONObject(updateParams);
			ObjectMapper obj_mapper = new ObjectMapper();

			// Handle profile
			if (updatedParamsJson.has("profiles") && updatedParamsJson.get("profiles") != null) {
				List<Map<String, String>> profilelist = null;

				Map<String, Map<String, String>> profilemap = new HashMap<String, Map<String, String>>();
				try {
					// String te = updatedParamsJson.get("profiles").toString();
					profilelist = obj_mapper.readValue(updatedParamsJson.get("profiles").toString(),
							(new ArrayList<Map<String, String>>()).getClass());

					profilelist.stream().forEach(pro -> {
						profilemap.put(pro.get("objectId"), pro);
					});

				} catch (IOException e) {
					e.printStackTrace();
				}
				Set<Profile> updatedprofileset = new HashSet<Profile>();
				for (Map.Entry<String, Map<String, String>> entry : profilemap.entrySet()) {
					// updatedprofilelist.add(entry.getValue());
					Profile p = obj_mapper.convertValue(entry.getValue(), Profile.class);
					updatedprofileset.add(p);
				}
				// Get existing

				long startTime = System.nanoTime();
				InOutParameters inOutParameters = findById(userId, tenantId);
				long endTime = System.nanoTime();
				wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");
				UserProfile profile_temp = inOutParameters.getUserProfile();
				if (profile_temp.getProfiles() != null) {// If already profile exists, append new map
					List<Profile> Existing_profile = profile_temp.getProfiles();
					TreeSet<Profile> existingSet = new TreeSet<Profile>(new ProfileComparator());
					existingSet.addAll(updatedprofileset);
					existingSet.addAll(Existing_profile);
					/*
					 * Existing_profile.stream().forEach(p -> { profilemap.put(p.getObjectId(), p);
					 * });
					 */

					update.set("profiles", existingSet);
				} else {// add new
					update.set("profiles", updatedprofileset);
				}
				updatedParamsJson.remove("profiles");
			}

			Set<String> existingGroupIds = new HashSet<String>();

			if (updatedParamsJson.has("groupIds") && updatedParamsJson.get("groupIds") != null) {
				long startTime = System.nanoTime();
				InOutParameters inOutParameters = findById(userId, tenantId);
				long endTime = System.nanoTime();
				wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");
				UserProfile existingUserProfile = inOutParameters.getUserProfile();
				if (existingUserProfile != null) {
					if (existingUserProfile.getGroupIds() != null) {
						existingGroupIds.addAll(existingUserProfile.getGroupIds());
					}
				}
				List<String> groupList;
				groupList = obj_mapper.readValue(updatedParamsJson.get("groupIds").toString(), List.class);
				existingGroupIds.addAll(groupList);
				update.set("groupIds", existingGroupIds);
				updatedParamsJson.remove("groupIds");
			}

			Map<String, String> userprofileMap = null;
			try {
				userprofileMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Map.Entry<String, String> entry : userprofileMap.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}

		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}

		logger.debug("Updated User Profile : " + userProfile);

		Double resSize = 0.0;
		inOutParams.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0;
		}
		inOutParams.setResponsePayloadSize(resSize);
		inOutParams.setUserProfile(userProfile);
		return inOutParams;

	}

	@Override
	public UserProfile findAndModifyByGroup(String userId, Map<String, Object> updateParams, String tenantId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("userId").is(userId));

		// JSONObject json = new JSONObject(updateParamsJson);
		// Map<String, Object> map = json.toMap();

		/*
		 * IntStream.range(0,json.length()).forEach(counter->{ if
		 * ("id".equalsIgnoreCase(json.get.getKey()) ||
		 * "version".equalsIgnoreCase(entry.getKey()) ||
		 * "creationDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "accessDateTime".equalsIgnoreCase(entry.getKey()) ||
		 * "revisedDateTime".equalsIgnoreCase(entry.getKey())) { continue; }
		 * update.set(entry.getKey(), entry.getValue()); });
		 */
		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		if (updateParams != null) {
			JSONObject updatedParamsJson = new JSONObject(updateParams);
			ObjectMapper obj_mapper = new ObjectMapper();

			// Handle profile
			if (updatedParamsJson.has("profiles") && updatedParamsJson.get("profiles") != null) {
				List<Map<String, String>> profilelist = null;

				Map<String, Map<String, String>> profilemap = new HashMap<String, Map<String, String>>();
				try {
					// String te = updatedParamsJson.get("profiles").toString();
					profilelist = obj_mapper.readValue(updatedParamsJson.get("profiles").toString(),
							(new ArrayList<Map<String, String>>()).getClass());

					profilelist.stream().forEach(pro -> {
						profilemap.put(pro.get("objectId"), pro);
					});

				} catch (IOException e) {
					e.printStackTrace();
				}
				Set<Profile> updatedprofileset = new HashSet<Profile>();
				for (Map.Entry<String, Map<String, String>> entry : profilemap.entrySet()) {
					// updatedprofilelist.add(entry.getValue());
					Profile p = obj_mapper.convertValue(entry.getValue(), Profile.class);
					updatedprofileset.add(p);
				}
				// Get existing
				long startTime = System.nanoTime();
				InOutParameters inOutParameters = findById(userId, tenantId);
				long endTime = System.nanoTime();
				wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
						inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
						"UserGroupService");
				UserProfile profile_temp = inOutParameters.getUserProfile();
				if (profile_temp.getProfiles() != null) {// If already profile exists, append new map
					List<Profile> Existing_profile = profile_temp.getProfiles();
					TreeSet<Profile> existingSet = new TreeSet<Profile>(new ProfileComparator());
					existingSet.addAll(updatedprofileset);
					existingSet.addAll(Existing_profile);
					/*
					 * Existing_profile.stream().forEach(p -> { profilemap.put(p.getObjectId(), p);
					 * });
					 */

					update.set("profiles", existingSet);
				} else {// add new
					update.set("profiles", updatedprofileset);
				}
				updatedParamsJson.remove("profiles");
			}

			Map<String, String> userprofileMap = null;
			try {
				userprofileMap = obj_mapper.readValue(updatedParamsJson.toString(), Map.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Map.Entry<String, String> entry : userprofileMap.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}

		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}

		logger.debug("Updated User Profile : " + userProfile);
		return userProfile;
	}
	//

	@Override
	public InOutParameters findAll(Map<String, String[]> paramMap, String tenantId) {

		Query query = new Query();
		InOutParameters inOutParams = new InOutParameters();
		List<UserProfile> userProfileList = null;
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		if (paramMap.containsKey("profiles.favourite")) {
			query.addCriteria(Criteria.where("profiles.favourite")
					.is(Boolean.parseBoolean(paramMap.get("profiles.favourite")[0])));
		}
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey()) || "tenantId".equalsIgnoreCase(entry.getKey())
					|| "profiles.favourite".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				// query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0],
				// "UTF-8")));
				if (entry.getValue().length == 1) {
					query.addCriteria(
							Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
				} else if (entry.getValue().length > 1) {
					query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		try {
			userProfileList = mongoTemplate.find(query, UserProfile.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			userProfileList = mongoTemplate.find(query, UserProfile.class);
		}
		Double resSize = 0.0;
		if (userProfileList != null) {
			resSize = (GraphLayout.parseInstance(userProfileList).totalSize()) / 1024.0;
		}
		inOutParams.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParams.setResponsePayloadSize(resSize);
		inOutParams.setUserProfileList(userProfileList);
		return inOutParams;
	}

	@Override
	public InOutParameters findAllFavourites(Map<String, String[]> paramMap, String tenantId) {

		Query query = new Query();
		List<UserProfile> userProfileList;
		InOutParameters inOutParams = new InOutParameters();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("profiles.favourite").is(true));
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
					|| "creationDateTime".equalsIgnoreCase(entry.getKey())
					|| "accessDateTime".equalsIgnoreCase(entry.getKey()) || "tenantId".equalsIgnoreCase(entry.getKey())
					|| "profiles.favourite".equalsIgnoreCase(entry.getKey())
					|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			try {
				logger.debug(entry.getKey() + "=>" + URLDecoder.decode(entry.getValue()[0], "UTF-8"));
				query.addCriteria(Criteria.where(entry.getKey()).is(URLDecoder.decode(entry.getValue()[0], "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		try {
			userProfileList = mongoTemplate.find(query, UserProfile.class);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			logger.debug("Exception thrown---------retrying action.... ");
			userProfileList = mongoTemplate.find(query, UserProfile.class);
		}

		Double resSize = 0.0;
		if (userProfileList != null) {
			resSize = (GraphLayout.parseInstance(userProfileList).totalSize()) / 1024.0;
		}
		inOutParams.setRequestPayloadSize((GraphLayout.parseInstance(query).totalSize()) / 1024.0);
		inOutParams.setResponsePayloadSize(resSize);
		inOutParams.setUserProfileList(userProfileList);
		return inOutParams;
	}

	@Override
	public InOutParameters findAndDeallocateGroupFromUserId(String tenantId, String userId, String[] groupIds)
			throws CustomException {
		InOutParameters inOutParams = new InOutParameters();
		Map<String, Object> updateParams = new HashMap<String, Object>();
		// updateParams.put("userIds", userId);

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(userId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		Set<Object> existingGroupIds = new HashSet<Object>();

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = findById(userId, tenantId);
		long endTime = System.nanoTime();
		wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
				inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
				"UserGroupService");
		UserProfile existingUserProfile = inOutParameters.getUserProfile();
		if (existingUserProfile == null) {
			exceptionThrower.throwGroupIdNotPresent();
		}
		existingGroupIds.addAll(existingUserProfile.getGroupIds());

		if (existingGroupIds.size() > 0) {
			int originalSize = existingGroupIds.size();

			Arrays.asList(groupIds).forEach(groupId -> {
				if (existingGroupIds.contains(groupId)) {
					existingGroupIds.remove(groupId);
				}
			});

			if (originalSize == existingGroupIds.size()) {
				exceptionThrower.throwUserIdNotPresentInThisGroup();
			}
		} else {
			exceptionThrower.throwNoUserIdNotPresentInThisGroup();
		}

		updateParams.put("groupIds", existingGroupIds);

		if (updateParams != null) {
			for (Map.Entry<String, Object> entry : updateParams.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}
		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}

		/*
		 * if (existingGroupIds.size() > 0) { for (Object group : existingGroupIds) {
		 * GroupUserAssociation groupUserAssociation =
		 * hybridRoleRightGroupUserAssociationDao .findById(group.toString(), tenantId);
		 * if (groupUserAssociation != null) {
		 * 
		 * groupUserAssociation.getGroupIds().remove(groupId);
		 * 
		 * hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(group.toString(),
		 * groupUserAssociation, tenantId); } } }
		 */

		Double resSize = 0.0;
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0;
		}
		inOutParams.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		inOutParams.setResponsePayloadSize(resSize);
		inOutParams.setUserProfile(userProfile);
		return inOutParams;
	}

	@Override
	public InOutParameters findAndDeallocateProfileFromUserId(String tenantId, String userId, String[] objectIds)
			throws CustomException {
		InOutParameters inOutParams = new InOutParameters();
		Map<String, Object> updateParams = new HashMap<String, Object>();
		// updateParams.put("userIds", userId);

		Query query = new Query();
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(userId));

		Update update = new Update();
		update.set("revisedDateTime", new Date());
		update.set("accessDateTime", new Date());

		long startTime = System.nanoTime();
		InOutParameters inOutParameters = findById(userId, tenantId);
		long endTime = System.nanoTime();
		wrapperServiceImpl.callLoggingService(tenantId, null, "CosmosDB", startTime, endTime,
				inOutParameters.getRequestPayloadSize(), inOutParameters.getResponsePayloadSize(), "GET",
				"UserGroupService");

		UserProfile existingUserProfile = inOutParameters.getUserProfile();
		if (existingUserProfile == null) {
			exceptionThrower.throwGroupIdNotPresent();
		}

		Set<Profile> existingProfiles = new HashSet<Profile>(existingUserProfile.getProfiles());

		// existinguserids.addAll(existingGroupProfile.getUserIds());

		Set<Profile> removeProfiles = new HashSet<Profile>();

		if (existingProfiles.size() > 0) {
			int originalSize = existingProfiles.size();

			Arrays.asList(objectIds).forEach(objectId -> {
				existingProfiles.forEach(profile -> {
					if (profile.getObjectId().equals(objectId)) {
						removeProfiles.add(profile);
					}
				});

			});

			existingProfiles.removeAll(removeProfiles);

			if (originalSize == existingProfiles.size()) {
				exceptionThrower.throwUserIdNotPresentInThisGroup();
			}
		} else {
			exceptionThrower.throwNoUserIdNotPresentInThisGroup();
		}

		updateParams.put("profiles", existingProfiles);

		if (updateParams != null) {
			for (Map.Entry<String, Object> entry : updateParams.entrySet()) {
				if ("id".equalsIgnoreCase(entry.getKey()) || "version".equalsIgnoreCase(entry.getKey())
						|| "creationDateTime".equalsIgnoreCase(entry.getKey())
						|| "accessDateTime".equalsIgnoreCase(entry.getKey())
						|| "revisedDateTime".equalsIgnoreCase(entry.getKey())) {
					continue;
				}
				update.set(entry.getKey(), entry.getValue());
			}
		}
		UserProfile userProfile = null;
		try {
			userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
					new FindAndModifyOptions().returnNew(true), UserProfile.class);
		} catch (UncategorizedMongoDbException ex) {
			// TODO Auto-generated catch block
			logger.error(ex.getMessage());
		} finally {
			if (userProfile == null) {
				logger.debug("Exception thrown---------retrying action.... ");
				userProfile = (UserProfile) mongoTemplate.findAndModify(query, update,
						new FindAndModifyOptions().returnNew(true), UserProfile.class);
			}
			logger.debug(" " + userProfile);
		}

		/*
		 * if (existinguserids.size() > 0) { for (Object user : existinguserids) {
		 * GroupUserAssociation groupUserAssociation =
		 * hybridRoleRightGroupUserAssociationDao .findById(user.toString(), tenantId);
		 * if (groupUserAssociation != null) {
		 * 
		 * groupUserAssociation.getGroupIds().remove(groupId);
		 * 
		 * hybridRoleRightGroupUserAssociationDao.findAndModifyByGroup(user.toString(),
		 * groupUserAssociation, tenantId); } } }
		 */

		Double resSize = 0.0;
		if (userProfile != null) {
			resSize = (GraphLayout.parseInstance(userProfile).totalSize()) / 1024.0;
		}
		inOutParams.setRequestPayloadSize(((GraphLayout.parseInstance(query).totalSize()) / 1024.0)
				+ ((GraphLayout.parseInstance(update).totalSize()) / 1024.0));
		inOutParams.setResponsePayloadSize(resSize);
		inOutParams.setUserProfile(userProfile);
		return inOutParams;
	}

}
